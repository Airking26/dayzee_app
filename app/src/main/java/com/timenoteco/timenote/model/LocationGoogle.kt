package com.timenoteco.timenote.model

import com.google.gson.annotations.SerializedName


data class LocationGoogle (

	@SerializedName("lat") val lat : Double,
	@SerializedName("lng") val lng : Double
)