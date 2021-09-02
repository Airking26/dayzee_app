package com.dayzeeco.dayzee.webService.service

import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.common.channel_id
import com.dayzeeco.dayzee.common.type
import com.dayzeeco.dayzee.model.*
import com.dayzeeco.dayzee.webService.repo.DayzeeRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.GsonBuilder
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

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val type = message.data[type]?.toInt()

        val broadcastIntent = Intent("NotificationOnClickListener")
        if(type != 1){
            broadcastIntent.putExtra("user", GsonBuilder().create().fromJson(message.data["user"], UserInfoDTO::class.java))
            if(type == 0 || type == 6) broadcastIntent.putExtra("event", GsonBuilder().create().fromJson(message.data["event"], TimenoteInfoDTO::class.java))
        }
        broadcastIntent.putExtra("type", type)
        val intent = PendingIntent.getBroadcast(
            applicationContext,
            0,
            broadcastIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
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

   /* private fun extractTimenoteInfoDTO(data: JSONObject, s: String?): TimenoteInfoDTO{
        val ki = GsonBuilder().create().fromJson(s, TimenoteInfoDTO::class.java)
        val createdBy = data["createdBy"] as JSONObject
        val user = extractUserInfoDTO(createdBy)
        val id = data["id"] as String
        val createdAt = data["createdAt"] as String
        val title = data["title"] as String
        val k = data["organizers"] as String
        val j = GsonBuilder().create().fromJson(k, Array<UserInfoDTO>::class.java).toList()
        val description = if(data.has("description")) data["description"] as String else null
        val organizers = if(data.has("organizers")) data["organizers"] as List<UserInfoDTO> else listOf()
        val pictures = if (data.has("pictures")) data["pictures"] as List<String> else null
        val colorHex = if(data.has("colorHex")) data["colorHex"] as String else null
        val startingAt = data["startingAt"] as String
        val endingAt = data["endingAt"] as String
        val urlTitle = if(data.has("urlTitle")) data["urlTitle"] as String else null
        val url = if(data.has("url")) data["url"] as String else null
        val likedBy = if(data.has("likedBy")) data["likedBy"] as Int else 0
        val commentAccount = if(data.has("commentAccount")) data["commentAccount"] as Int else 0
        val isParticipating = data["isParticipating"] as Boolean
        val hashtags = if(data.has("hashtags")) data["hashtags"] as List<String> else null
        val category: Category? = if(data.has("Category")){
            val categoryObj = data["Category"] as JSONObject
            Category(category = categoryObj["category"] as String, subcategory = categoryObj["subcategory"] as String)
        } else null
        val joinedBy: JoinedBy? = if(data.has("joinedBy")){
            val joinedByObject = data["joinedBy"] as JSONObject
            JoinedBy(users = joinedByObject["users"] as List<UserInfoDTO>, count = joinedByObject["count"] as Int)
        } else null
        val priceObject = data["price"] as JSONObject
        val price = Price(price = priceObject["price"] as Int, currency = priceObject["currency"] as String)
        var locationObj: Location? = null
        if (data.has("location")) {
            val location = data["location"] as JSONObject
            val latitude = if (location.has("latitude")) location["latitude"] as Double else 0.0
            val longitude = if (location.has("longitude")) location["longitude"] as Double else 0.0
            val addressObj =
                if (location.has("address")) location["address"] as JSONObject else null
            if (addressObj != null) {
                val address = if (addressObj.has("address")) addressObj["address"] as String else ""
                val zipCode = if (addressObj.has("zipCode")) addressObj["zipCode"] as String else ""
                val city = if (addressObj.has("city")) addressObj["city"] as String else ""
                val country = if (addressObj.has("country")) addressObj["country"] as String else ""
                locationObj = Location(
                    longitude,
                    latitude,
                    Address(address, zipCode, city, country)
                )
            }
        } else locationObj = null
        return TimenoteInfoDTO(createdBy = user, id = id, createdAt = createdAt, title = title, description = description,
        organizers = organizers, pictures = pictures, colorHex = colorHex, location = locationObj, startingAt = startingAt,
        endingAt = endingAt, likedBy = likedBy, urlTitle = urlTitle, url = url, commentAccount = commentAccount, isParticipating = isParticipating,
            hashtags = hashtags, category = category, price = price, joinedBy = joinedBy
        )

    }

    private fun extractUserInfoDTO(data: JSONObject): UserInfoDTO {
        val socialMedias = data["socialMedias"] as JSONObject
        val youtube = socialMedias["youtube"] as JSONObject
        val whatsApp = socialMedias["whatsApp"] as JSONObject
        val instagram = socialMedias["instagram"] as JSONObject
        val linkedin = socialMedias["linkedIn"] as JSONObject
        val facebook = socialMedias["facebook"] as JSONObject
        var locationObj: Location? = null
        if (data.has("location")) {
            val location = data["location"] as JSONObject
            val latitude = if (location.has("latitude")) location["latitude"] as Double else 0.0
            val longitude = if (location.has("longitude")) location["longitude"] as Double else 0.0
            val addressObj =
                if (location.has("address")) location["address"] as JSONObject else null
            if (addressObj != null) {
                val address = if (addressObj.has("address")) addressObj["address"] as String else ""
                val zipCode = if (addressObj.has("zipCode")) addressObj["zipCode"] as String else ""
                val city = if (addressObj.has("city")) addressObj["city"] as String else ""
                val country = if (addressObj.has("country")) addressObj["country"] as String else ""
                locationObj = Location(
                    longitude,
                    latitude,
                    Address(address, zipCode, city, country)
                )
            }
        } else locationObj = null
        return UserInfoDTO(
            data["id"] as String,
            data["email"] as String,
            data["userName"] as String,
            picture = if (data.has("picture")) data["picture"] as String else null,
            dateFormat = data["dateFormat"] as Int,
            givenName = if (data.has("givenName")) data["givenName"] as String else null,
            familyName = if (data.has("familyName")) data["familyName"] as String else null,
            birthday = if (data.has("birthday")) data["birthday"] as String else null,
            gender = if (data.has("gender")) data["gender"] as String else null,
            description = if (data.has("description")) data["description"] as String else null,
            status = data["status"] as Int,
            followers = data["followers"] as Int,
            following = data["following"] as Int,
            isInFollowers = data["isInFollowers"] as Boolean,
            isInFollowing = data["isInFollowing"] as Boolean,
            isAdmin = data["isAdmin"] as Boolean,
            certified = data["certified"] as Boolean,
            createdAt = data["createdAt"] as String,
            language = data["language"] as String,
            location = locationObj,
            socialMedias = SocialMedias(
                Youtube(youtube["url"] as String, youtube["enabled"] as Boolean),
                Facebook(facebook["url"] as String, facebook["enabled"] as Boolean),
                Instagram(instagram["url"] as String, instagram["enabled"] as Boolean),
                WhatsApp(whatsApp["url"] as String, whatsApp["enabled"] as Boolean),
                LinkedIn(linkedin["url"] as String, linkedin["enabled"] as Boolean)
            )
        )
    }*/

}