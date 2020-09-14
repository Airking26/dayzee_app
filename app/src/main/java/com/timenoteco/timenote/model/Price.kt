package com.timenoteco.timenote.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Price (
    @SerializedName("value") var price: Int,
    @SerializedName("currency") var currency: String
) : Parcelable