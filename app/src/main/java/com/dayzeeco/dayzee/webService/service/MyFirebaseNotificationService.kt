package com.dayzeeco.dayzee.webService.service

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.NavDeepLinkBuilder
import androidx.preference.PreferenceManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.common.*
import com.dayzeeco.dayzee.model.Notification
import com.dayzeeco.dayzee.model.UserInfoDTO
import com.dayzeeco.dayzee.view.MainActivity
import com.dayzeeco.dayzee.view.homeFlow.DetailedTimenoteArgs
import com.dayzeeco.dayzee.viewModel.FollowViewModel
import com.dayzeeco.dayzee.webService.repo.DayzeeRepository
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import java.lang.reflect.Type
import kotlin.coroutines.coroutineContext

class MyFirebaseNotificationService : FirebaseMessagingService() {

    private lateinit var prefs : SharedPreferences
    private var meService: MeService = DayzeeRepository().getMeService()

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

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        /*test data */
        val id: String? = if(message.data[type]?.toInt() == 2 || message.data[type]?.toInt() == 3 || message.data[type]?.toInt() == 4){
            message.data[user_id]
        } else {
            message.data[timenote_id]
        }

        val x = message.data["user"] ?: ""
        val o = prefs.getString(user_info_dto, "")
        val hx = message.notification?.body?: ""
        if(!x.isEmpty() && !x.isBlank()){
            val typeUserInfo: Type = object : TypeToken<UserInfoDTO?>() {}.type
            val ki = JSONObject(x)
            val m = ki.toString()
            var map: Map<String, Any> = HashMap()
            val o = Gson().fromJson<UserInfoDTO?>(x, typeUserInfo)
            val k = ""
        }

        /*val body = message.data[body] ?: ""
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
        bundle.putString(user_picture_url, pictureUrl)*/


        val pi = NavDeepLinkBuilder(this)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.navigation_graph_tab_home)
            .setDestination(R.id.detailedTimenote)
            .setArguments(DetailedTimenoteArgs.Builder(0, null).build().toBundle())
            .createPendingIntent()


        val builder = NotificationCompat.Builder(this, channel_id)
            .setSmallIcon(R.drawable.ic_icon_launcher_dayzee)
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