package com.dayzeeco.dayzee.model

import com.google.gson.annotations.SerializedName

data class RootUserResponse (
	@SerializedName("user") val user : UserInfoDTO,
	@SerializedName("accessToken") val token : String,
	@SerializedName("refreshToken") val refreshToken: String
)