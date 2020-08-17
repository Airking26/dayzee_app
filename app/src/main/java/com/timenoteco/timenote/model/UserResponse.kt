package com.timenoteco.timenote.model
import com.google.gson.annotations.SerializedName

data class UserResponse (

	@SerializedName("id") val id : String,
	@SerializedName("email") val email : String,
	@SerializedName("givenName") val givenName : String,
	@SerializedName("familyName") val familyName : String,
	@SerializedName("createdAt") val createdAt : String
)