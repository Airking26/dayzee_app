package com.dayzeeco.dayzee.view.profileFlow

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.adapter.NotificationAdapter
import com.dayzeeco.dayzee.adapter.NotificationComparator
import com.dayzeeco.dayzee.adapter.NotificationPagingAdapter
import com.dayzeeco.dayzee.adapter.TimenoteLoadStateAdapter
import com.dayzeeco.dayzee.common.accessToken
import com.dayzeeco.dayzee.common.user_info_dto
import com.dayzeeco.dayzee.model.NotificationInfoDTO
import com.dayzeeco.dayzee.model.UserInfoDTO
import com.dayzeeco.dayzee.viewModel.*
import kotlinx.android.synthetic.main.fragment_notifications.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.reflect.Type

class Notifications : Fragment(), NotificationAdapter.NotificationClickListener {

    private var userInfoDTO: UserInfoDTO? = null
    private lateinit var prefs : SharedPreferences
    private lateinit var notificationAdapter: NotificationPagingAdapter
    private val timenoteViewModel : TimenoteViewModel by activityViewModels()
    private val authViewModel : LoginViewModel by activityViewModels()
    private val meViewModel : MeViewModel by activityViewModels()
    private val followViewModel: FollowViewModel by activityViewModels()
    private val notificationViewModel: NotificationViewModel by activityViewModels()
    private var tokenId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        tokenId = prefs.getString(accessToken, null)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_notifications, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val typeUserInfo: Type = object : TypeToken<UserInfoDTO?>() {}.type
        userInfoDTO = Gson().fromJson<UserInfoDTO>(prefs.getString(user_info_dto, ""), typeUserInfo)

            notificationAdapter = NotificationPagingAdapter(NotificationComparator, this)

            notifications_rv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = notificationAdapter.withLoadStateFooter(
                    footer = TimenoteLoadStateAdapter { notificationAdapter.retry() }
                )
                addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
            }

        lifecycleScope.launch {
            notificationViewModel.getNotifications(tokenId!!, userInfoDTO?.id!!, prefs).collectLatest {
                notificationAdapter.submitData(it)
            }
        }
    }

    override fun onNotificationClicked(notification: NotificationInfoDTO) {
        if(notification.type == 0 || notification.type == 1|| notification.type == 6 || notification.type == 5){
            timenoteViewModel.getSpecificTimenote(tokenId!!, notification.idData).observe(viewLifecycleOwner, Observer {
                if(it.code() == 401){
                    authViewModel.refreshToken(prefs).observe(viewLifecycleOwner, { newAccessToken ->
                        tokenId = newAccessToken
                        timenoteViewModel.getSpecificTimenote(tokenId!!, notification.idData).observe(viewLifecycleOwner,
                            { timenoteInfoDTO ->
                                if(timenoteInfoDTO.isSuccessful) findNavController().navigate(NotificationsDirections.actionGlobalDetailedTimenote(1, it.body()))

                            })
                    })
                }
                if(it.isSuccessful)
                findNavController().navigate(NotificationsDirections.actionGlobalDetailedTimenote(1, it.body()))
            })
        } else {
            meViewModel.getSpecificUser(tokenId!!, notification.idData).observe(viewLifecycleOwner, {
                if(it.code() == 401){
                    authViewModel.refreshToken(prefs).observe(viewLifecycleOwner, { newAccessToken ->
                        tokenId = newAccessToken
                        meViewModel.getSpecificUser(tokenId!!, notification.idData).observe(viewLifecycleOwner,
                            { userInfoDTO ->
                                if(userInfoDTO.isSuccessful) findNavController().navigate(NotificationsDirections.actionGlobalProfileElse(4).setUserInfoDTO(it.body()))

                            })
                    })
                }
                if(it.isSuccessful)
                findNavController().navigate(NotificationsDirections.actionGlobalProfileElse(4).setUserInfoDTO(it.body()))
            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onAcceptedRequestClicked(notification: NotificationInfoDTO) {
        followViewModel.acceptFollowingRequest(tokenId!!, notification.idData).observe(viewLifecycleOwner,
            {
                if(it.code() == 401){
                    authViewModel.refreshToken(prefs).observe(viewLifecycleOwner, { newAccessToken ->
                        tokenId = newAccessToken
                        followViewModel.acceptFollowingRequest(tokenId!!, notification.idData).observe(viewLifecycleOwner,
                            { userInfoDTO ->
                                if(userInfoDTO.isSuccessful) {
                                    notificationViewModel.deleteNotification(tokenId!!, notification.id).observe(viewLifecycleOwner,
                                        { rd ->
                                            if(rd.isSuccessful) {
                                                notificationAdapter.notifyDataSetChanged()
                                                notificationAdapter.refresh()
                                            }
                                        })
                                }
                            })
                    })
                }
                if(it.isSuccessful) {
                    notificationViewModel.deleteNotification(tokenId!!, notification.id).observe(viewLifecycleOwner, {
                            rd -> if(rd.isSuccessful) notificationAdapter.notifyDataSetChanged()
                    })
                    Thread.sleep(5000)
                    notificationAdapter.notifyDataSetChanged()
                    notificationAdapter.refresh()
                }

            })
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onDeclinedRequestClicked(notification: NotificationInfoDTO) {
        followViewModel.declineFollowingRequest(tokenId!!, notification.idData).observe(viewLifecycleOwner,
            {
                if(it.code() == 401){
                    authViewModel.refreshToken(prefs).observe(viewLifecycleOwner, { newAccessToken ->
                        tokenId = newAccessToken
                        followViewModel.declineFollowingRequest(tokenId!!, notification.idData).observe(viewLifecycleOwner,
                            { userInfoDTO ->
                                if(userInfoDTO.isSuccessful){
                                    notificationViewModel.deleteNotification(tokenId!!, notification.id).observe(viewLifecycleOwner,
                                        { rd ->
                                            if(rd.isSuccessful) {
                                                notificationAdapter.notifyDataSetChanged()
                                                notificationAdapter.refresh()
                                            }
                                        })
                                }
                            })
                    })
                }

                if(it.isSuccessful) {
                    notificationViewModel.deleteNotification(tokenId!!, notification.id).observe(viewLifecycleOwner, {
                            rd -> if(rd.isSuccessful) notificationAdapter.notifyDataSetChanged()
                    })
                    Thread.sleep(5000)
                    notificationAdapter.notifyDataSetChanged()
                    notificationAdapter.refresh()
                }

            })
    }


}
