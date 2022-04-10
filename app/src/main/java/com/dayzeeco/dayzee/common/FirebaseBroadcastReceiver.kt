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

    private lateinit var prefs : SharedPreferences
    val TAG: String = FirebaseBroadcastReceiver::class.java.simpleName

    override fun onReceive(context: Context, intent: Intent) {

        prefs = PreferenceManager.getDefaultSharedPreferences(context)

        val dataBundle = intent.extras
        if (dataBundle != null)
            for (key in dataBundle.keySet()) {
                Log.d(TAG, "dataBundle: " + key + " : " + dataBundle.get(key))
            }
    }
}