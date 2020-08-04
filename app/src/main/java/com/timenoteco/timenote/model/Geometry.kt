package com.timenoteco.timenote.model

import com.google.gson.annotations.SerializedName

data class Geometry (

	@SerializedName("location") val location : LocationGoogle,
	@SerializedName("viewport") val viewport : Viewport
)