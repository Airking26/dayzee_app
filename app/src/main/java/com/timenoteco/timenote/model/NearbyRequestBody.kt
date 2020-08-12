package com.timenoteco.timenote.model

import com.google.gson.annotations.SerializedName

data class NearbyRequestBody (
    @SerializedName("location") val location : Location,
    @SerializedName("maxDistance") val maxDistance : Int,
    @SerializedName("categories") val categories : Categories,
    @SerializedName("date") val date : String,
    @SerializedName("price") val price : Int,
    @SerializedName("type") val type : String
)