package com.timenoteco.timenote.model

import com.google.gson.annotations.SerializedName

data class ShareTimenoteDTO (

	@SerializedName("timenote") val timenote : String,
	@SerializedName("users") val users : List<String>
)