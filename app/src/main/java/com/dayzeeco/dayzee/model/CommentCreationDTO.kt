package com.dayzeeco.dayzee.model

import com.google.gson.annotations.SerializedName


data class CommentCreationDTO (
	@SerializedName("createdBy") val createdBy : String,
	@SerializedName("timenote") val timenote : String,
	@SerializedName("description") val description : String,
	@SerializedName("hashtags") val hashtags : List<String>?,
	@SerializedName("tagged") val tagged: List<String>?,
	@SerializedName("picture") val picture: String?
)