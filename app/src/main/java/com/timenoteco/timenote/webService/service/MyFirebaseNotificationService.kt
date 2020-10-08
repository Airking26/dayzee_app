package com.timenoteco.timenote.webService.service

import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import androidx.activity.viewModels
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.timenoteco.timenote.R
import com.timenoteco.timenote.model.FCMDTO
import com.timenoteco.timenote.model.Notification
import com.timenoteco.timenote.model.UserInfoDTO
import com.timenoteco.timenote.view.profileFlow.Notifications
import com.timenoteco.timenote.viewModel.MeViewModel
import java.lang.reflect.Type

class MyFirebaseNotificationService : FirebaseMessagingService() {

    //private var intent = Intent(this, Notifications::class.java).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK }
    //val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
    private lateinit var prefs : SharedPreferences
    private val CHANNEL_ID: String = "dayzee_channel"
    private var notifications: MutableList<Notification> = mutableListOf()

    override fun onCreate() {
        super.onCreate()
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.edit().putString("notifications", Gson().toJson(prefs.getString("notifications", null))).apply()
    }

    override fun onNewToken(token: String)  = sendRegistrationToserver(token)

    private fun sendRegistrationToserver(token: String) {
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val typeNotification: Type = object : TypeToken<MutableList<Notification?>>() {}.type
        notifications = Gson().fromJson<MutableList<Notification>>(prefs.getString("notifications", null), typeNotification)
        notifications.add(Notification(false, message.messageId!!, System.currentTimeMillis(), null))

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("Dayzee")
            .setContentText(message.messageId)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message.messageId))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            //.setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)){
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }

}