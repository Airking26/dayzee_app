package com.timenoteco.timenote.webService.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseNotificationService : FirebaseMessagingService() {

    override fun onNewToken(token: String)  = sendRegistrationToserver(token)

    private fun sendRegistrationToserver(token: String) {}

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        val o = p0.messageId
    }

}