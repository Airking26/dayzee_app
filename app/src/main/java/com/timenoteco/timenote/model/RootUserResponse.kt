package com.timenoteco.timenote.model

import com.google.gson.annotations.SerializedName

const val accessToken: String = "ACCESS_TOKEN"
const val refreshToken: String = "REFRESH_TOKEN"

data class RootUserResponse (
	@SerializedName("user") val user : UserInfoDTO,
	@SerializedName("accessToken") val token : String,
	@SerializedName("refreshToken") val refreshToken: String
)