package com.timenoteco.timenote.model

import com.google.gson.annotations.SerializedName

data class RootUserResponse (

	@SerializedName("user") val user : UserInfoDTO,
	@SerializedName("token") val token : String
)