package com.dayzeeco.dayzee.model

import com.google.gson.annotations.SerializedName

data class AlarmCreationDTO (
	@SerializedName("createdBy") val createdBy : String,
	@SerializedName("timenote") val timenote : String,
	@SerializedName("date") val date : String
)