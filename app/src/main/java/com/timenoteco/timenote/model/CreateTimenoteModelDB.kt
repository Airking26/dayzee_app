package com.timenoteco.timenote.model

import com.google.gson.annotations.SerializedName

data class CreateTimenoteModelDB (

	@SerializedName("createdBy") var createdBy : String,
	@SerializedName("title") var title : String,
	@SerializedName("description") var description : String,
	@SerializedName("colorHex") var colorHex : String,
	@SerializedName("location") var location : Location,
	@SerializedName("category") var category : Category,
	@SerializedName("startingAt") var startingAt : String,
	@SerializedName("endingAt") var endingAt : String,
	@SerializedName("hashtags") var hashtags : List<String>,
	@SerializedName("url") var url : String,
	@SerializedName("price") var price : Int,
	@SerializedName("status") var status : String
)