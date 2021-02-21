package com.dayzeeco.dayzee.model

import com.google.gson.annotations.SerializedName

data class TimenoteFilteredDTO (

	@SerializedName("upcoming") val upcoming : Boolean,
	@SerializedName("alarm") val alarm : Boolean,
	@SerializedName("created") val created : Boolean,
	@SerializedName("joined") val joined : Boolean,
	@SerializedName("sharedWith") val sharedWith : Boolean
)