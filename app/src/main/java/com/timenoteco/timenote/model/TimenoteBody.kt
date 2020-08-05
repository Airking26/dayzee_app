package com.timenoteco.timenote.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TimenoteBody (

    @SerializedName("createdAt") val createdAt : String,
    @SerializedName("createdBy") val createdBy : CreatedBy,
    @SerializedName("title") val title : String,
    @SerializedName("description") val description : String,
    @SerializedName("pictures") val pictures : List<String>,
    @SerializedName("colorHex") val colorHex : String,
    @SerializedName("location") val location : Location,
    @SerializedName("category") val category : Category,
    @SerializedName("startingAt") val startingAt : String,
    @SerializedName("endingAt") val endingAt : String,
    @SerializedName("hashtags") val hashtags : List<String>,
    @SerializedName("url") val url : String,
    @SerializedName("price") val price : Int,
    @SerializedName("status") val status : String
) : Parcelable