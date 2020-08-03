package com.timenoteco.timenote.model

import com.google.gson.annotations.SerializedName

data class CreateTimenoteModelDB (

	@SerializedName("createdBy") val createdBy : String,
	@SerializedName("title") val title : String,
	@SerializedName("description") val description : String,
	@SerializedName("colorHex") val colorHex : String,
	@SerializedName("location") val location : Location,
	@SerializedName("category") val category : Category,
	@SerializedName("startingAt") val startingAt : String,
	@SerializedName("endingAt") val endingAt : String,
	@SerializedName("hashtags") val hashtags : List<String>,
	@SerializedName("url") val url : String,
	@SerializedName("price") val price : Int,
	@SerializedName("status") val status : String
)