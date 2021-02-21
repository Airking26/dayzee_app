package com.dayzeeco.dayzee.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class JoinedBy (
    @SerializedName("friends") val users : List<UserInfoDTO>,
    @SerializedName("total") val count : Int
) : Parcelable