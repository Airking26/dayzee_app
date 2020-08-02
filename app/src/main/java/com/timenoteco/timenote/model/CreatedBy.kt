package com.timenoteco.timenote.model

import com.google.gson.annotations.SerializedName

data class CreatedBy (

	@SerializedName("id") val id : String,
	@SerializedName("email") val email : String,
	@SerializedName("userName") val userName : String,
	@SerializedName("pictureURL") val pictureURL : String,
	@SerializedName("givenName") val givenName : String,
	@SerializedName("familyName") val familyName : String,
	@SerializedName("createdAt") val createdAt : String
)