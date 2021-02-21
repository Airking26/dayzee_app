package com.dayzeeco.dayzee.model

import com.google.gson.annotations.SerializedName

data class TimenoteCreationSignalementDTO(
    @SerializedName("createdBy") val createdBy : String,
    @SerializedName("timenote") val timenoteId: String,
    @SerializedName("description") val desc : String
)