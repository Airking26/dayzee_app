package com.timenoteco.timenote.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NearbyRequestBody (
    @SerializedName("location") var location : Location,
    @SerializedName("maxDistance") var maxDistance : Int,
    @SerializedName("categories") var categories : List<Categories>? = listOf(),
    @SerializedName("date") var date : String,
    @SerializedName("price") var price : Price,
    @SerializedName("type") var type : Int,
    @SerializedName("userID") var userID: String? = null
) : Parcelable