package com.timenoteco.timenote.model

import com.google.gson.annotations.SerializedName


data class Address (

	@SerializedName("address") val address : String,
	@SerializedName("zipCode") val zipCode : String,
	@SerializedName("city") val city : String,
	@SerializedName("country") val country : String
)