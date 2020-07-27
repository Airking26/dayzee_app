package com.timenoteco.timenote.model

import com.google.gson.annotations.SerializedName

data class TimenoteCreationModel (

	@SerializedName("createdBy") val createdBy : String,
	@SerializedName("title") val title : String,
	@SerializedName("description") val description : String?,
	@SerializedName("pictures") val pictures : List<String>?,
	@SerializedName("colorHex") val colorHex : String?,
	@SerializedName("startingAt") val startingAt : String,
	@SerializedName("endingAt") val endingAt : String,
	@SerializedName("hashtags") val hashtags : List<String>?,
	@SerializedName("price") val price : Int?,
	@SerializedName("url") val url : String?
)