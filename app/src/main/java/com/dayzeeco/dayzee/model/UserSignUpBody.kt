package com.dayzeeco.dayzee.model

import com.google.gson.annotations.SerializedName

data class UserSignUpBody (

	@SerializedName("email") val email : String,
	@SerializedName("userName") val username : String,
	@SerializedName("password") val password : String,
	@SerializedName("ip") val ip: String,
	@SerializedName("platform") val platform: String
)