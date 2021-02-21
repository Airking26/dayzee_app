package com.dayzeeco.dayzee.model

import com.google.gson.annotations.SerializedName

data class PreferenceResultDTO (
	@SerializedName("category") val category : Category,
	@SerializedName("users") val users : List<UserInfoDTO>,
	@SerializedName("rating") val rating : Int
)