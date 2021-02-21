package com.dayzeeco.dayzee.model

import com.google.gson.annotations.SerializedName

data class AlarmInfoDTO (
	@SerializedName("id") val id : String,
	@SerializedName("timenote") val timenote : String,
	@SerializedName("date") val date : String
)