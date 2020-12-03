package com.timenoteco.timenote.view.searchFlow

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.ExperimentalPagingApi
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.UsersAwaitingPagingAdapter
import com.timenoteco.timenote.adapter.UsersPagingAdapter
import com.timenoteco.timenote.adapter.UsersShareWithPagingAdapter
import com.timenoteco.timenote.model.UserInfoDTO
import com.timenoteco.timenote.model.accessToken
import com.timenoteco.timenote.view.profileFlow.FollowPageDirections
import com.timenoteco.timenote.viewModel.FollowViewModel
import com.timenoteco.timenote.viewModel.LoginViewModel
import kotlinx.android.synthetic.main.fragment_follow_page.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FollowPageSearch : Fragment(), UsersPagingAdapter.SearchPeopleListener, UsersAwaitingPagingAdapter.AcceptDecline,
    UsersAwaitingPagingAdapter.SearchPeopleListener {

    private val followViewModel : FollowViewModel by activityViewModels()
    private val authViewModel : LoginViewModel by activityViewModels()
    private lateinit var usersPagingAdapter: UsersPagingAdapter
    private lateinit var usersAwaitingPagingAdapter: UsersAwaitingPagingAdapter
    private lateinit var prefs: SharedPreferences
    private var tokenId: String? = null
    private val args: FollowPageSearchArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(accessToken, null)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_follow_page, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        when(args.followers){
            0 -> followPage_toolbar.text = getString(R.string.followers)
            1 -> followPage_toolbar.text = getString(R.string.following)
            2 -> followPage_toolbar.text = getString(R.string.waiting_for_approval)
            3 -> followPage_toolbar.text = getString(R.string.asked_sent)
        }

        if(args.followers == 0 || args.followers == 1 || args.followers == 3){
            usersPagingAdapter = UsersPagingAdapter(
                UsersPagingAdapter.UserComparator,
                null,
                this,
                null,
                args.followers
            )
            users_rv.layoutManager = LinearLayoutManager(requireContext())
            users_rv.adapter = usersPagingAdapter
            if(args.followers == 3) {
                lifecycleScope.launch{
                    followViewModel.getUsersAskedToFollow(tokenId!!, prefs).collectLatest {
                        usersPagingAdapter.submitData(it)
                    }
                }
            } else {
                lifecycleScope.launch{
                    followViewModel.getUsers(tokenId!!, args.id, followers = args.followers, sharedPreferences = prefs).collectLatest {
                        usersPagingAdapter.submitData(it)
                    }
                }
            }
        } else if(args.followers == 2) {
            usersAwaitingPagingAdapter = UsersAwaitingPagingAdapter(UsersShareWithPagingAdapter.UserComparator, null, this, this)
            users_rv.layoutManager = LinearLayoutManager(requireContext())
            users_rv.adapter = usersAwaitingPagingAdapter
            if(args.followers == 2){
                lifecycleScope.launch {
                    followViewModel.getUsersWaitingForApproval(tokenId!!, prefs).collectLatest {
                        usersAwaitingPagingAdapter.submitData(it)
                    }
                }
            }
        }
    }

    override fun onSearchClicked(userInfoDTO: UserInfoDTO) {
        findNavController().navigate(FollowPageSearchDirections.actionFollowPageSearchToProfileSearch(userInfoDTO))
    }

    override fun onUnfollow(id: String) {
    }

    override fun onRemove(id: String) {

    }

    @ExperimentalPagingApi
    override fun onAccept(userInfoDTO: UserInfoDTO, position: Int) {
        followViewModel.acceptFollowingRequest(tokenId!!, userInfoDTO.id!!).observe(viewLifecycleOwner, Observer {
            if(it.code() == 401){
                authViewModel.refreshToken(prefs).observe(viewLifecycleOwner, Observer { newAccessToken ->
                    tokenId = newAccessToken
                    followViewModel.acceptFollowingRequest(tokenId!!, userInfoDTO.id!!).observe(viewLifecycleOwner, Observer {usrInfoDTO ->
                        if(usrInfoDTO.isSuccessful){
                            usersAwaitingPagingAdapter.refresh()
                        }
                    })
                })
            }
            if(it.isSuccessful){
                usersAwaitingPagingAdapter.refresh()
            }
        })
    }

    override fun onDecline(userInfoDTO: UserInfoDTO, position: Int) {
        followViewModel.declineFollowingRequest(tokenId!!, userInfoDTO.id!!).observe(viewLifecycleOwner, Observer {
            if(it.code() == 401){
                authViewModel.refreshToken(prefs).observe(viewLifecycleOwner, Observer { newAccessToken ->
                    tokenId = newAccessToken
                    followViewModel.declineFollowingRequest(tokenId!!, userInfoDTO.id!!).observe(viewLifecycleOwner, Observer {usrInfoDTO ->
                        if(usrInfoDTO.isSuccessful) usersAwaitingPagingAdapter.refresh()
                    })
                })
            }
            if(it.isSuccessful){
                usersAwaitingPagingAdapter.refresh()
            }
        })
    }
}