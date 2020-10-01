package com.timenoteco.timenote.view.profileFlow

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
import com.timenoteco.timenote.model.TimenoteInfoDTO
import com.timenoteco.timenote.model.UserInfoDTO
import com.timenoteco.timenote.viewModel.FollowViewModel
import com.timenoteco.timenote.viewModel.ProfileViewModel
import kotlinx.android.synthetic.main.fragment_follow_page.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FollowPage : Fragment(), UsersPagingAdapter.SearchPeopleListener,
    UsersAwaitingPagingAdapter.SearchPeopleListener, UsersAwaitingPagingAdapter.AcceptDecline {

    private val followViewModel : FollowViewModel by activityViewModels()
    private lateinit var usersPagingAdapter: UsersPagingAdapter
    private lateinit var usersAwaitingPagingAdapter: UsersAwaitingPagingAdapter
    private lateinit var prefs: SharedPreferences
    private val args : FollowPageArgs by navArgs()
    val TOKEN: String = "TOKEN"
    private var tokenId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(TOKEN, null)
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
        usersPagingAdapter = UsersPagingAdapter(UsersPagingAdapter.UserComparator, null,this)
        users_rv.layoutManager = LinearLayoutManager(requireContext())
        users_rv.adapter = usersPagingAdapter
            if(args.followers == 3) {
                lifecycleScope.launch{
                    followViewModel.getUsersAskedToFollow(tokenId!!).collectLatest {
                        usersPagingAdapter.submitData(it)
                    }
                }
            } else {
                lifecycleScope.launch{
                    followViewModel.getUsers(tokenId!!, followers = args.followers).collectLatest {
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
                    followViewModel.getUsersWaitingForApproval(tokenId!!).collectLatest {
                        usersAwaitingPagingAdapter.submitData(it)
                    }
                }
            }
        }
    }

    override fun onSearchClicked(userInfoDTO: UserInfoDTO) {
        findNavController().navigate(FollowPageDirections.actionFollowPageToProfile().setFrom(4).setWhereFrom(true).setUserInfoDTO(userInfoDTO))
    }

    @ExperimentalPagingApi
    override fun onAccept(userInfoDTO: UserInfoDTO, position: Int) {
        followViewModel.acceptFollowingRequest(tokenId!!, userInfoDTO.id!!).observe(viewLifecycleOwner, Observer {
            if(it.isSuccessful){
                usersAwaitingPagingAdapter.refresh()
                //usersAwaitingPagingAdapter.notifyItemRemoved(position)
            }
        })
    }

    override fun onDecline(userInfoDTO: UserInfoDTO, position: Int) {
        followViewModel.declineFollowingRequest(tokenId!!, userInfoDTO.id!!).observe(viewLifecycleOwner, Observer {
            if(it.isSuccessful){
                usersAwaitingPagingAdapter.refresh()
                //usersAwaitingPagingAdapter.notifyItemRemoved(position)
            }
        })
    }
}