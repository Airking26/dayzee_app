package com.timenoteco.timenote.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Location(
    @SerializedName("longitude") var longitude: Double,
    @SerializedName("latitude") var latitude: Double,
    @SerializedName("address") var address: Address
): Parcelable