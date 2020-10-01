package com.timenoteco.timenote.view.profileFlow

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.NotificationAdapter
import com.timenoteco.timenote.model.Notification
import kotlinx.android.synthetic.main.fragment_notifications.*
import java.lang.reflect.Type

class Notifications : Fragment() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_notifications, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val typeNotification: Type = object : TypeToken<MutableList<Notification?>>() {}.type
        //notifications = Gson().fromJson<MutableList<Notification>>(prefs.getString("notifications", null), typeNotification)
        notificationAdapter = NotificationAdapter(sortNotifications())
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

}
