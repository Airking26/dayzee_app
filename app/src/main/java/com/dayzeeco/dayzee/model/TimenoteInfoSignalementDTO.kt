package com.dayzeeco.dayzee.model

import com.google.gson.annotations.SerializedName

data class TimenoteInfoSignalementDTO(
    @SerializedName("createdBy") val createdBy : UserInfoDTO,
    @SerializedName("timenote") val timenote: TimenoteInfoDTO,
    @SerializedName("description") val desc : String,
    @SerializedName("createdAt") val createdAt: String
)