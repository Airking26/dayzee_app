package com.timenoteco.timenote.model

import com.google.gson.annotations.SerializedName

data class FilterLocationDTO (
    @SerializedName("location") val location: Location,
    @SerializedName("maxDistance") val maxDistance: Int
)