package com.timenoteco.timenote.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Notification (
    var read: Boolean,
    var message: String,
    var time: Long,
    var userInfoDTO: UserInfoDTO?
) : Parcelable