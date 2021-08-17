package com.dayzeeco.dayzee.model

import com.google.gson.annotations.SerializedName


data class NotificationInfoDTO (
	@SerializedName("id") val id : String,
	@SerializedName("createdAt") val createdAt : String,
	@SerializedName("hasBeenRead") val hasBeenRead : Boolean,
	@SerializedName("picture") val picture : String,
	@SerializedName("type") val type : Int,
	@SerializedName("belongTo") val belongTo : String,
	@SerializedName("username") val username : String,
	@SerializedName("idData") val idData : String,
	@SerializedName("eventName") val eventName: String? = ""
)