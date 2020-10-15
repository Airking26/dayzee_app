package com.timenoteco.timenote.webService.service

import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.Navigation
import androidx.preference.PreferenceManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.timenoteco.timenote.R
import com.timenoteco.timenote.model.FCMDTO
import com.timenoteco.timenote.model.Notification
import com.timenoteco.timenote.model.UserInfoDTO
import com.timenoteco.timenote.view.MainActivity
import com.timenoteco.timenote.view.profileFlow.Notifications
import com.timenoteco.timenote.viewModel.MeViewModel
import java.lang.reflect.Type

class MyFirebaseNotificationService : FirebaseMessagingService() {

    private lateinit var prefs : SharedPreferences
    private val CHANNEL_ID: String = "dayzee_channel"
    private var notifications: MutableList<Notification> = mutableListOf()

    override fun onCreate() {
        super.onCreate()
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val typeNotification: Type = object : TypeToken<MutableList<Notification?>>() {}.type
        notifications = Gson().fromJson<MutableList<Notification>>(prefs.getString("notifications", null), typeNotification) ?: mutableListOf()
    }

    override fun onNewToken(token: String)  =
        sendRegistrationToserver(token)

    private fun sendRegistrationToserver(token: String) {
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val n = message.notification
        val m  = message.data

        Log.d(TAG, "onMessageReceived: RECEIVED!!!!!!!!!!!!!!!!!")

        val bundle = Bundle()
        bundle.putString("id", message.data["userID"])
        bundle.putString("type", message.data["type"])

        val pi = NavDeepLinkBuilder(this)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.navigation_graph_tab_profile)
            .setDestination(R.id.profile)
            .setArguments(bundle)
            .createPendingIntent()

        //notifications.add(Notification(false, message.messageId!!, message.sentTime, message.data["type"]!!, message.data["userID"]!!, message.notification?.title!!, message.notification?.body!!))
        //prefs.edit().putString("notifications", Gson().toJson(notifications) ?: Gson().toJson(mutableListOf<Notification>())).apply()

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(message.notification?.title)
            .setContentText(message.notification?.body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message.notification?.body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pi)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)){
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }

}