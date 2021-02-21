package com.dayzeeco.dayzee.model

import com.google.gson.annotations.SerializedName

data class CommentInfoDTO (

	@SerializedName("id") val id : String,
	@SerializedName("createdAt") val createdAt : String,
	@SerializedName("createdBy") val createdBy : UserInfoDTO,
	@SerializedName("description") val description : String,
	@SerializedName("hashtags") val hashtags : List<String>,
	@SerializedName("likedBy") val likedBy : Int
)