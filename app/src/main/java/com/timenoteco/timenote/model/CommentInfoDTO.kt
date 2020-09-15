package com.timenoteco.timenote.model

import com.google.gson.annotations.SerializedName

data class CommentInfoDTO (

	@SerializedName("id") val id : String,
	@SerializedName("createdAt") val createdAt : String,
	@SerializedName("createdBy") val createdBy : CreatedBy,
	@SerializedName("description") val description : String,
	@SerializedName("hashtags") val hashtags : List<String>,
	@SerializedName("likedBy") val likedBy : Int
)