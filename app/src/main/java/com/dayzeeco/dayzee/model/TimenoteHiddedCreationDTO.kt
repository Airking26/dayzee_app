package com.dayzeeco.dayzee.model

import com.google.gson.annotations.SerializedName

data class TimenoteHiddedCreationDTO (
    @SerializedName("createdBy") val createdBy: String,
    @SerializedName("timenote") val timenote: String? = null,
    @SerializedName("user") val user: String? = null
)