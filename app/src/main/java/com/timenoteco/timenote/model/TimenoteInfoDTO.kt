package com.timenoteco.timenote.model

import com.google.gson.annotations.SerializedName
import com.timenoteco.timenote.model.Category
import com.timenoteco.timenote.model.CreatedBy
import com.timenoteco.timenote.model.Location


data class TimenoteInfoDTO (

	@SerializedName("id") val id : String,
	@SerializedName("createdAt") val createdAt : String,
	@SerializedName("createdBy") val createdBy : CreatedBy,
	@SerializedName("organizers") val organizers : List<UserInfoDTO>,
	@SerializedName("title") val title : String,
	@SerializedName("description") val description : String,
	@SerializedName("pictures") val pictures : List<String>,
	@SerializedName("colorHex") val colorHex : String,
	@SerializedName("location") val location : Location,
	@SerializedName("category") val category : Category,
	@SerializedName("startingAt") val startingAt : String,
	@SerializedName("endingAt") val endingAt : String,
	@SerializedName("hashtags") val hashtags : List<String>,
	@SerializedName("url") val url : String,
	@SerializedName("price") val price : Int,
	@SerializedName("likedBy") val likedBy : Int,
	@SerializedName("joinedBy") val joinedBy : JoinedBy
)