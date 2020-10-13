package com.timenoteco.timenote.view.profileFlow

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.timenoteco.timenote.viewModel.TimenoteViewModel
import kotlinx.android.synthetic.main.fragment_notifications.*
import java.lang.reflect.Type

class Notifications : Fragment(), NotificationAdapter.NotificationClickListener {

    private lateinit var prefs : SharedPreferences
    private lateinit var notificationAdapter: NotificationAdapter
    private var notifications: MutableList<Notification>? = mutableListOf(
        Notification(true, "Ronny wants to follow you", 0L, null),
        Notification(true, "Ronny wants to follow you", 0L, null),
        Notification(true, "Ronny wants to follow you", 0L, null),
        Notification(true, "Ronny wants to follow you", 0L, null),
        Notification(true, "Ronny wants to follow you", 0L, null),
        Notification(true, "Ronny wants to follow you", 0L, null),
        Notification(true, "Ronny wants to follow you", 0L, null),
        Notification(true, "Ronny wants to follow you", 0L, null),
        Notification(true, "Ronny wants to follow you", 0L, null),
        Notification(true, "Ronny wants to follow you", 0L, null),
        Notification(true, "Ronny wants to follow you", 0L, null),
        Notification(true, "Ronny wants to follow you", 0L, null),
        Notification(true, "Ronny wants to follow you", 0L, null),
        Notification(true, "Ronny wants to follow you", 0L, null),
        Notification(true, "Ronny wants to follow you", 0L, null),
        Notification(true, "Ronny wants to follow you", 0L, null),
        Notification(true, "Ronny wants to follow you", 0L, null),
        Notification(true, "Ronny wants to follow you", 0L, null),
        Notification(true, "Ronny wants to follow you", 0L, null),
        Notification(true, "Ronny wants to follow you", 0L, null),
        Notification(true, "Ronny wants to follow you", 0L, null),
        Notification(false, "Ronny wants to follow you, accept or decline", 0L, null),
        Notification(false, "Samuel share a timenote with you", 0L, null),
        Notification(false, "Jordan has accepted your ask", 0L, null),
        Notification(false, "Aziz just commented your timenote", 0L, null),
        Notification(false, "Ronny wants to follow you, accept or decline", 0L, null),
        Notification(false, "Samuel share a timenote with you", 0L, null),
        Notification(false, "Jordan has accepted your ask", 0L, null),
        Notification(false, "Aziz just commented your timenote", 0L, null),
        Notification(false, "Ronny wants to follow you, accept or decline", 0L, null),
        Notification(false, "Samuel share a timenote with you", 0L, null),
        Notification(false, "Jordan has accepted your ask", 0L, null),
        Notification(false, "Aziz just commented your timenote", 0L, null),
        Notification(false, "Ronny wants to follow you", 0L, null),
        Notification(false, "Ronny wants to follow you", 0L, null),
        Notification(false, "Ronny wants to follow you", 0L, null),
        Notification(false, "Ronny wants to follow you", 0L, null),
        Notification(false, "Ronny wants to follow you", 0L, null),
        Notification(false, "Ronny wants to follow you", 0L, null),
        Notification(false, "Ronny wants to follow you", 0L, null),
        Notification(false, "Ronny wants to follow you", 0L, null),
        Notification(false, "Ronny wants to follow you", 0L, null),
        Notification(false, "Ronny wants to follow you", 0L, null),
        Notification(false, "Ronny wants to follow you", 0L, null),
        Notification(false, "Ronny wants to follow you", 0L, null),
        Notification(false, "Ronny wants to follow you", 0L, null),
        Notification(false, "Ronny wants to follow you", 0L, null),
        Notification(false, "Ronny wants to follow you", 0L, null)
    )
    private val timenoteViewModel : TimenoteViewModel by activityViewModels()
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
        //notifications = Gson().fromJson<MutableList<Notification>>(prefs.getString("notifications", null), typeNotification)
        notificationAdapter = NotificationAdapter(sortNotifications(), this)
        notifications_rv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = notificationAdapter
            addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        }
        //refreshNotifications()
    }

    fun sortNotifications(): MutableList<Notification> {
        val notificationsUnread = notifications?.filter { !it.read }
        val notificationReadedLastTen = notifications?.filter { it.read }?.takeLast(10)
        val notifs = notificationsUnread?.plus(notificationReadedLastTen!!)
        prefs.edit().putString("notifications", Gson().toJson(notifs?.toMutableList())).apply()
        return notifs?.toMutableList()!!
    }

    fun refreshNotifications(){
        val notifsNowReaden = notifications?.map { it.read = true }
        prefs.edit().putString("notifications", Gson().toJson(notifsNowReaden?.toMutableList())).apply()
    }

    override fun onNotificationClicked(notification: Notification) {
        if(true){
            timenoteViewModel.getSpecificTimenote(tokenId!!, "").observe(viewLifecycleOwner, Observer {
                NotificationsDirections.actionNotificationsToDetailedTimenote(1, it.body())
            })
        } else {

        }
    }

}
