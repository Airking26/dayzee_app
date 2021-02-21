package com.dayzeeco.dayzee.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Notification (
    var read: Boolean,
    var message: String,
    var time: Long,
    var type: String,
    var id: String,
    var title: String,
    var body: String,
    var pictureUrl: String
) : Parcelable