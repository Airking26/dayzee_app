package com.dayzeeco.dayzee.model

import com.google.gson.annotations.SerializedName

data class HiddedTimenoteInfoDTO (
    @SerializedName("user") val user: UserInfoDTO,
    @SerializedName("timenote") val timenote: TimenoteInfoDTO,
    @SerializedName("id") val id: String,
    @SerializedName("createdAt") val createdAt: String
    )