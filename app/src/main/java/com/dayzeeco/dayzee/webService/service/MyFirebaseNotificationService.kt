package com.dayzeeco.dayzee.webService.service

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
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.common.*
import com.dayzeeco.dayzee.model.Notification
import com.dayzeeco.dayzee.view.MainActivity
import java.lang.reflect.Type

class MyFirebaseNotificationService : FirebaseMessagingService() {

    private lateinit var prefs : SharedPreferences
    private var notifications: MutableList<Notification> = mutableListOf()

    override fun onCreate() {
        super.onCreate()
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val typeNotification: Type = object : TypeToken<MutableList<Notification?>>() {}.type
        notifications = Gson().fromJson<MutableList<Notification>>(prefs.getString(
            notifications_saved, null), typeNotification) ?: mutableListOf()
    }

    override fun onNewToken(token: String)  = sendRegistrationToserver(token)

    private fun sendRegistrationToserver(token: String) {
        val myIntent = Intent("FBR-IMAGE")
        myIntent.putExtra("token", token)
        this.sendBroadcast(myIntent)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        /*test data */
        val id: String? = if(message.data[type]?.toInt() == 2 || message.data[type]?.toInt() == 3 || message.data[type]?.toInt() == 4){
            message.data[user_id]
        } else {
            message.data[timenote_id]
        }
        val body = message.data[body] ?: ""
        val type = message.data[type] ?: ""
        val pictureUrl = message.data[user_picture_url] ?: ""
        val title = message.data[title] ?: ""

        val bundle = Bundle()
        bundle.putString(com.dayzeeco.dayzee.common.id, id)
        bundle.putString(com.dayzeeco.dayzee.common.type, type)
        bundle.putString("google.sent_time", message.sentTime.toString())
        bundle.putString("google.message_id", message.messageId)
        bundle.putString(com.dayzeeco.dayzee.common.type, title)
        bundle.putString(com.dayzeeco.dayzee.common.body, body)
        bundle.putString(user_picture_url, pictureUrl)


        val pi = NavDeepLinkBuilder(this)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.navigation_graph_tab_profile)
            .setDestination(R.id.myProfile)
            .setArguments(bundle)
            .createPendingIntent()

        if(notifications.none { notification -> notification.id == id }) {
            notifications.add(
                Notification(
                    false,
                    message.messageId!!,
                    message.sentTime,
                    type,
                    id!!,
                    title,
                    body,
                    pictureUrl
                )
            )
            prefs.edit().putString(
                notifications_saved,
                Gson().toJson(notifications) ?: Gson().toJson(mutableListOf<Notification>())
            ).apply()
        }

        val builder = NotificationCompat.Builder(this, channel_id)
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