package com.dayzeeco.dayzee.model

import com.google.gson.annotations.SerializedName

data class CreateGroupDTO (

	@SerializedName("name") val name : String,
	@SerializedName("users") val users : List<String>
)