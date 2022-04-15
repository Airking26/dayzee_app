package com.dayzeeco.dayzee.webService.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.common.channel_id
import com.dayzeeco.dayzee.common.type
import com.dayzeeco.dayzee.model.*
import com.dayzeeco.dayzee.viewModel.LoginViewModel
import com.dayzeeco.dayzee.webService.repo.DayzeeRepository
import com.google.api.client.googleapis.util.Utils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.GsonBuilder
import kotlinx.coroutines.android.awaitFrame
import org.json.JSONObject


class MyFirebaseNotificationService : FirebaseMessagingService() {

    private lateinit var prefs : SharedPreferences

    override fun onCreate() {
        super.onCreate()
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
    }

    override fun onNewToken(token: String)  = sendRegistrationToserver(token)

    private fun sendRegistrationToserver(token: String) {
        val myIntent = Intent("FBR-IMAGE")
        myIntent.putExtra("token", token)
        this.sendBroadcast(myIntent)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("LaunchActivityFromNotification")
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val type = message.data[type]?.toInt()

        val broadcastIntent = Intent("NotificationOnClickListener")
        if(type != 1){
            broadcastIntent.putExtra("userID", message.data["userID"])
            if(type == 0 || type == 6 || type == 7) broadcastIntent.putExtra("eventID", message.data["eventID"])
        }
        broadcastIntent.putExtra("type", type)
        val intent = PendingIntent.getBroadcast(
            applicationContext,
            0,
            broadcastIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        val builder = NotificationCompat.Builder(this, channel_id)
            .setSmallIcon(R.drawable.ic_stat_notif)
            .setContentTitle(message.notification?.title)
            .setContentText(message.notification?.body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message.notification?.body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(intent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)){
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }
}