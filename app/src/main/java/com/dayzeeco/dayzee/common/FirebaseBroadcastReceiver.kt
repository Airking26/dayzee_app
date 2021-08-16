package com.dayzeeco.dayzee.common

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.legacy.content.WakefulBroadcastReceiver
import androidx.preference.PreferenceManager
import com.dayzeeco.dayzee.model.Notification
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import kotlin.reflect.typeOf

class FirebaseBroadcastReceiver : WakefulBroadcastReceiver() {

    private var notifications: MutableList<Notification> = mutableListOf()
    private lateinit var prefs : SharedPreferences
    val TAG: String = FirebaseBroadcastReceiver::class.java.simpleName

    override fun onReceive(context: Context, intent: Intent) {

        prefs = PreferenceManager.getDefaultSharedPreferences(context)

        val dataBundle = intent.extras
        if (dataBundle != null)
            for (key in dataBundle.keySet()) {
                Log.d(TAG, "dataBundle: " + key + " : " + dataBundle.get(key))
            }
        val remoteMessage = RemoteMessage(dataBundle)

        /*val id: String? = if(remoteMessage.data[type]?.toInt() == 2 || remoteMessage.data[type]?.toInt() == 3 || remoteMessage.data[type]?.toInt() == 4){
            remoteMessage.data[user_id]
        } else {
            remoteMessage.data[timenote_id]
        }
        val body = remoteMessage.data[body] ?: ""
        val type = remoteMessage.data[type] ?: ""
        val pictureUrl = remoteMessage.data[user_picture_url] ?: ""
        val title = remoteMessage.data[title] ?: ""

        val typeNotification: Type = object : TypeToken<MutableList<Notification?>>() {}.type
        notifications = Gson().fromJson<MutableList<Notification>>(prefs.getString(
            notifications_saved, null), typeNotification) ?: mutableListOf()
        if(notifications.none { notification -> notification.id == id }) {
            notifications.add(
                Notification(
                    false,
                    remoteMessage.messageId!!,
                    remoteMessage.sentTime,
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
        }*/
    }
}