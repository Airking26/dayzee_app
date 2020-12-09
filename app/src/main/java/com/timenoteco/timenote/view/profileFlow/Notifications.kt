package com.timenoteco.timenote.view.profileFlow

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
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.NotificationAdapter
import com.timenoteco.timenote.common.stringLiveData
import com.timenoteco.timenote.model.Notification
import com.timenoteco.timenote.model.accessToken
import com.timenoteco.timenote.viewModel.FollowViewModel
import com.timenoteco.timenote.viewModel.LoginViewModel
import com.timenoteco.timenote.viewModel.MeViewModel
import com.timenoteco.timenote.viewModel.TimenoteViewModel
import kotlinx.android.synthetic.main.fragment_notifications.*
import java.lang.reflect.Type

class Notifications : Fragment(), NotificationAdapter.NotificationClickListener {

    private lateinit var prefs : SharedPreferences
    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var notifications: MutableList<Notification>
    private val timenoteViewModel : TimenoteViewModel by activityViewModels()
    private val authViewModel : LoginViewModel by activityViewModels()
    private val meViewModel : MeViewModel by activityViewModels()
    private val followViewModel: FollowViewModel by activityViewModels()
    private var tokenId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        tokenId = prefs.getString(accessToken, null)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_notifications, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        prefs.stringLiveData("notifications", Gson().toJson(prefs.getString("notifications", null))).observe(viewLifecycleOwner, Observer {
            val typeNotification: Type = object : TypeToken<MutableList<Notification?>>() {}.type
            notifications = Gson().fromJson<MutableList<Notification>>(it, typeNotification) ?: mutableListOf()

            notificationAdapter = NotificationAdapter(sortNotifications(), this)
            notifications_rv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = notificationAdapter
                addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
            }
        })


    }

    private fun sortNotifications(): MutableList<Notification> {
        val notificationsUnread = notifications.filter { !it.read }.sortedBy { notification -> notification.time }.asReversed()
        val notificationReadedLastTen = notifications.filter { it.read }.sortedBy { notification -> notification.time }.takeLast(10).asReversed()
        val notifs = notificationsUnread.plus(notificationReadedLastTen)
        prefs.edit().putString("notifications", Gson().toJson(notifs.toMutableList())).apply()
        return notifs.toMutableList()
    }

    private fun refreshNotifications(){
        val notification = notifications
        notifications.map { it.read = true }
        prefs.edit().putString("notifications", Gson().toJson(notifications)).apply()
    }

    override fun onNotificationClicked(notification: Notification) {
        if(notification.type.toInt() == 0 || notification.type.toInt() == 1){
            timenoteViewModel.getSpecificTimenote(tokenId!!, notification.id).observe(viewLifecycleOwner, Observer {
                if(it.code() == 401){
                    authViewModel.refreshToken(prefs).observe(viewLifecycleOwner, Observer { newAccessToken ->
                        tokenId = newAccessToken
                        timenoteViewModel.getSpecificTimenote(tokenId!!, notification.id).observe(viewLifecycleOwner, Observer {timenoteInfoDTO ->
                            if(timenoteInfoDTO.isSuccessful) findNavController().navigate(NotificationsDirections.actionGlobalDetailedTimenote(1, it.body()))

                        })
                    })
                }
                if(it.isSuccessful)
                findNavController().navigate(NotificationsDirections.actionGlobalDetailedTimenote(1, it.body()))
            })
        } else {
            meViewModel.getSpecificUser(tokenId!!, notification.id).observe(viewLifecycleOwner, Observer {
                if(it.code() == 401){
                    authViewModel.refreshToken(prefs).observe(viewLifecycleOwner, Observer {newAccessToken ->
                        tokenId = newAccessToken
                        meViewModel.getSpecificUser(tokenId!!, notification.id).observe(viewLifecycleOwner, Observer {userInfoDTO ->
                            if(userInfoDTO.isSuccessful) findNavController().navigate(NotificationsDirections.actionGlobalProfile().setFrom(4).setIsNotMine(true).setUserInfoDTO(it.body()))

                        })
                    })
                }
                if(it.isSuccessful)
                findNavController().navigate(NotificationsDirections.actionGlobalProfile().setFrom(4).setIsNotMine(true).setUserInfoDTO(it.body()))
            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onAcceptedRequestClicked(notification: Notification) {
        followViewModel.acceptFollowingRequest(tokenId!!, notification.id).observe(viewLifecycleOwner, Observer {
            if(it.code() == 401){
                authViewModel.refreshToken(prefs).observe(viewLifecycleOwner, Observer {newAccessToken ->
                    tokenId = newAccessToken
                    followViewModel.acceptFollowingRequest(tokenId!!, notification.id).observe(viewLifecycleOwner, Observer { userInfoDTO ->
                        if(userInfoDTO.isSuccessful) {
                            notifications.remove(notification)
                            prefs.edit().putString("notifications", Gson().toJson(notifications))
                                .apply()
                        }
                    })
                })
            }
            if(it.isSuccessful) {
                notifications.remove(notification)
                prefs.edit().putString("notifications", Gson().toJson(notifications)).apply()
            }

        })
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onDeclinedRequestClicked(notification: Notification) {
        followViewModel.declineFollowingRequest(tokenId!!, notification.id).observe(viewLifecycleOwner, Observer {
            if(it.code() == 401){
                authViewModel.refreshToken(prefs).observe(viewLifecycleOwner, Observer { newAccessToken ->
                    tokenId = newAccessToken
                    followViewModel.declineFollowingRequest(tokenId!!, notification.id).observe(viewLifecycleOwner, Observer { userInfoDTO ->
                        if(userInfoDTO.isSuccessful){
                            notifications.remove(notification)
                            prefs.edit().putString("notifications", Gson().toJson(notifications)).apply()
                        }
                    })
                })
            }

            if(it.isSuccessful) {
                notifications.remove(notification)
                prefs.edit().putString("notifications", Gson().toJson(notifications)).apply()
            }

        })
    }


    override fun onDestroy() {
        super.onDestroy()
        refreshNotifications()
    }


}
