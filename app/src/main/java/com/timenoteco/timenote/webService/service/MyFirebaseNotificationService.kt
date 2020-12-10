package com.timenoteco.timenote.webService.service

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import androidx.preference.PreferenceManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.timenoteco.timenote.R
import com.timenoteco.timenote.model.Notification
import com.timenoteco.timenote.view.MainActivity
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
        val myIntent = Intent("FBR-IMAGE")
        myIntent.putExtra("token", token)
        this.sendBroadcast(myIntent)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        /*test data */
        val id: String? = if(message.data["type"]?.toInt() == 2 || message.data["type"]?.toInt() == 3 || message.data["type"]?.toInt() == 4){
            message.data["userID"]
        } else {
            message.data["timenoteID"]
        }
        val body = message.data["body"] ?: ""
        val type = message.data["type"] ?: ""
        val pictureUrl = message.data["userPictureURL"] ?: ""
        val title = message.data["title"] ?: ""

        val bundle = Bundle()
        bundle.putString("id", id)
        bundle.putString("type", type)
        bundle.putString("google.sent_time", message.sentTime.toString())
        bundle.putString("google.message_id", message.messageId)
        bundle.putString("title", title)
        bundle.putString("body", body)
        bundle.putString("userPictureURL", pictureUrl)


        val pi = NavDeepLinkBuilder(this)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.navigation_graph_tab_profile)
            .setDestination(R.id.myProfile)
            .setArguments(bundle)
            .createPendingIntent()

        notifications.add(Notification(false, message.messageId!!, message.sentTime, type, id!!, title, body, pictureUrl))
        prefs.edit().putString("notifications", Gson().toJson(notifications) ?: Gson().toJson(mutableListOf<Notification>())).apply()

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_notif)
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