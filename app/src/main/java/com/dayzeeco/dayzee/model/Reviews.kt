package com.dayzeeco.dayzee.model

import com.google.gson.annotations.SerializedName

data class Reviews (

	@SerializedName("author_name") val author_name : String,
	@SerializedName("author_url") val author_url : String,
	@SerializedName("language") val language : String,
	@SerializedName("profile_photo_url") val profile_photo_url : String,
	@SerializedName("rating") val rating : Int,
	@SerializedName("relative_time_description") val relative_time_description : String,
	@SerializedName("text") val text : String,
	@SerializedName("time") val time : Int
)