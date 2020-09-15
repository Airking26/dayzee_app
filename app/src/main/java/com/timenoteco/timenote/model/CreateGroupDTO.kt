package com.timenoteco.timenote.model

import com.google.gson.annotations.SerializedName

data class CreateGroupDTO (

	@SerializedName("name") val name : String,
	@SerializedName("users") val users : List<String>
)