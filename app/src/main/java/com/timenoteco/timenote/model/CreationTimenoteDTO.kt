package com.timenoteco.timenote.model

import com.google.gson.annotations.SerializedName

data class CreationTimenoteDTO (

	@SerializedName("createdBy") var createdBy : String,
	@SerializedName("organizers") var organizers : List<String>? = null,
	@SerializedName("title") var title : String,
	@SerializedName("description") var description : String? = null,
	@SerializedName("pictures") var pictures : List<String>? = null,
	@SerializedName("colorHex") var colorHex : String? = null,
	@SerializedName("location") var location : Location,
	@SerializedName("category") var category : Category? = null,
	@SerializedName("startingAt") var startingAt : String,
	@SerializedName("endingAt") var endingAt : String,
	@SerializedName("hashtags") var hashtags : List<String>? = null,
	@SerializedName("url") var url : String? = null,
	@SerializedName("price") var price : Int,
	@SerializedName("sharedWith") var sharedWith : List<String>? = null
)