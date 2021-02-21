package com.dayzeeco.dayzee.model

import com.google.gson.annotations.SerializedName

data class RefreshTokenDTO(
    @SerializedName("accessToken") val accessToken : String
)