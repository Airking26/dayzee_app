package com.timenoteco.timenote.model

import com.google.gson.annotations.SerializedName

enum class TypeOfNotification {
    TIMENOTESHARED,
    RINGALARM,
    FOLLOWED,
    ASKEDTOFOLLOW,
    ACCEPTEDFOLLOWASK
}

data class NotificationDTO (
    @SerializedName("type") val type : TypeOfNotification,
    @SerializedName("timenoteID") val timenoteID : String,
    @SerializedName("userID") val userID: String
)