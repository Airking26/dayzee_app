package com.timenoteco.timenote.view.profileFlow

import android.content.Context
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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.UsersAwaitingPagingAdapter
import com.timenoteco.timenote.adapter.UsersPagingAdapter
import com.timenoteco.timenote.adapter.UsersShareWithPagingAdapter
import com.timenoteco.timenote.listeners.GoToProfile
import com.timenoteco.timenote.model.UserInfoDTO
import com.timenoteco.timenote.model.accessToken
import com.timenoteco.timenote.viewModel.FollowViewModel
import com.timenoteco.timenote.viewModel.LoginViewModel
import kotlinx.android.synthetic.main.fragment_follow_page.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.reflect.Type

class FollowPage : Fragment(), UsersPagingAdapter.SearchPeopleListener,
    UsersAwaitingPagingAdapter.SearchPeopleListener, UsersAwaitingPagingAdapter.AcceptDecline {

    private lateinit var userInfoDTO: UserInfoDTO
    private lateinit var goToProfileLisner : GoToProfile
    private val followViewModel : FollowViewModel by activityViewModels()
    private val authViewModel : LoginViewModel by activityViewModels()
    private lateinit var usersPagingAdapter: UsersPagingAdapter
    private lateinit var usersAwaitingPagingAdapter: UsersAwaitingPagingAdapter
    private lateinit var prefs: SharedPreferences
    private val args : FollowPageArgs by navArgs()
    private var tokenId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(accessToken, null)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        goToProfileLisner = context as GoToProfile
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_follow_page, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val typeUserInfo: Type = object : TypeToken<UserInfoDTO?>() {}.type
        userInfoDTO = Gson().fromJson<UserInfoDTO>(prefs.getString("UserInfoDTO", ""), typeUserInfo)

        when(args.followers){
            0 -> followPage_toolbar.text = getString(R.string.following)
            1 -> followPage_toolbar.text = getString(R.string.followers)
            2 -> followPage_toolbar.text = getString(R.string.waiting_for_approval)
            3 -> followPage_toolbar.text = getString(R.string.asked_sent)
        }

        if(args.followers == 0 || args.followers == 1 || args.followers == 3){
        usersPagingAdapter = UsersPagingAdapter(UsersPagingAdapter.UserComparator, null,this, args.isNotMine, args.followers)
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
        if(userInfoDTO.id == this.userInfoDTO.id) goToProfileLisner.goToProfile()
        else findNavController().navigate(FollowPageDirections.actionGlobalProfileElse(args.from).setUserInfoDTO(userInfoDTO))
    }

    override fun onUnfollow(id: String) {
        followViewModel.unfollowUser(tokenId!!, id).observe(viewLifecycleOwner, Observer {
            if(it.code() == 401){
                authViewModel.refreshToken(prefs).observe(viewLifecycleOwner, Observer {newAccessToken ->
                    tokenId = newAccessToken
                    followViewModel.unfollowUser(tokenId!!, id).observe(viewLifecycleOwner, Observer {resp ->
                        if(resp.isSuccessful) usersPagingAdapter.refresh()
                    })
                })
            }
            if(it.isSuccessful) usersPagingAdapter.refresh()
        })
    }

    override fun onRemove(id: String) {
        followViewModel.removeUserFromFollower(tokenId!!, id).observe(viewLifecycleOwner, Observer {
            if(it.code() == 401){
                authViewModel.refreshToken(prefs).observe(viewLifecycleOwner, Observer { newAccessToken ->
                    tokenId = newAccessToken
                    followViewModel.removeUserFromFollower(tokenId!!, id).observe(viewLifecycleOwner, Observer { rsp ->
                        if(rsp.isSuccessful) usersPagingAdapter.refresh()

                    })
                })
            }
            if(it.isSuccessful) usersPagingAdapter.refresh()
        })
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