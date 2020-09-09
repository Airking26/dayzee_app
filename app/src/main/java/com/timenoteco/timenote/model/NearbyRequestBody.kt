package com.timenoteco.timenote.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NearbyRequestBody (
    @SerializedName("location") var location : Location,
    @SerializedName("maxDistance") var maxDistance : Int,
    @SerializedName("categories") var categories : Categories,
    @SerializedName("date") var date : String,
    @SerializedName("price") var price : Int,
    @SerializedName("type") var type : String
) : Parcelable