package com.dayzeeco.dayzee.model

import com.google.gson.annotations.SerializedName


data class DetailedPlace (

	@SerializedName("html_attributions") val html_attributions : List<String>,
	@SerializedName("result") val result : Result,
	@SerializedName("status") val status : String
)