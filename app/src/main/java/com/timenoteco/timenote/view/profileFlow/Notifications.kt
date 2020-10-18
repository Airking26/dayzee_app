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
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.NotificationAdapter
import com.timenoteco.timenote.model.Notification
import com.timenoteco.timenote.viewModel.FollowViewModel
import com.timenoteco.timenote.viewModel.MeViewModel
import com.timenoteco.timenote.viewModel.TimenoteViewModel
import kotlinx.android.synthetic.main.fragment_notifications.*
import java.lang.reflect.Type

class Notifications : Fragment(), NotificationAdapter.NotificationClickListener {

    private lateinit var prefs : SharedPreferences
    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var notifications: MutableList<Notification>
    private val timenoteViewModel : TimenoteViewModel by activityViewModels()
    private val meViewModel : MeViewModel by activityViewModels()
    private val followViewModel: FollowViewModel by activityViewModels()
    val TOKEN: String = "TOKEN"
    private var tokenId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        tokenId = prefs.getString(TOKEN, null)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_notifications, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val typeNotification: Type = object : TypeToken<MutableList<Notification?>>() {}.type
        notifications = Gson().fromJson<MutableList<Notification>>(prefs.getString("notifications", null), typeNotification) ?: mutableListOf()
        notificationAdapter = NotificationAdapter(sortNotifications(), this)
        notifications_rv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = notificationAdapter
            addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun sortNotifications(): MutableList<Notification> {
        val notificationsUnread = notifications.filter { !it.read }.sortedBy { notification -> notification.time }.asReversed()
        val notificationReadedLastTen = notifications.filter { it.read }.sortedBy { notification -> notification.time }.takeLast(10).asReversed()
        val notifs = notificationsUnread.plus(notificationReadedLastTen)
        prefs.edit().putString("notifications", Gson().toJson(notifs.toMutableList())).apply()
        return notifs.toMutableList()
    }

    private fun refreshNotifications(){
        notifications.map { it.read = true }
        prefs.edit().putString("notifications", Gson().toJson(notifications)).apply()
    }

    override fun onNotificationClicked(notification: Notification) {
        if(true){
            timenoteViewModel.getSpecificTimenote(tokenId!!, notification.id).observe(viewLifecycleOwner, Observer {
                NotificationsDirections.actionNotificationsToDetailedTimenote(1, it.body())
            })
        } else {
            meViewModel.getSpecificUser(tokenId!!, notification.id).observe(viewLifecycleOwner, Observer {
                NotificationsDirections.actionNotificationsToProfile().setFrom(4).setIsNotMine(false).setUserInfoDTO(it.body())
            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onAcceptedRequestClicked(id: String) {
        followViewModel.acceptFollowingRequest(tokenId!!, id).observe(viewLifecycleOwner, Observer {
            notifications.removeIf{ it.id == id }
            notificationAdapter.notifyDataSetChanged()
        })
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onDeclinedRequestClicked(id: String) {
        followViewModel.declineFollowingRequest(tokenId!!, id).observe(viewLifecycleOwner, Observer {
            notifications.removeIf{ it.id == id }
            notificationAdapter.notifyDataSetChanged()
        })
    }


    override fun onDestroy() {
        super.onDestroy()
        refreshNotifications()
    }


}
