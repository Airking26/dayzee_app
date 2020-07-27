package com.timenoteco.timenote.model

import com.google.gson.annotations.SerializedName

data class UserSignUpBody (

	@SerializedName("givenName") val givenName : String,
	@SerializedName("familyName") val familyName : String,
	@SerializedName("email") val email : String,
	@SerializedName("password") val password : String
)