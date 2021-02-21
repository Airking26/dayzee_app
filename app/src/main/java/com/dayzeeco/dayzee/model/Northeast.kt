package com.dayzeeco.dayzee.model

import com.google.gson.annotations.SerializedName


data class Northeast (

	@SerializedName("lat") val lat : Double,
	@SerializedName("lng") val lng : Double
)