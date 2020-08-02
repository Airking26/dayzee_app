package com.timenoteco.timenote.model

import com.google.gson.annotations.SerializedName


data class Category (

	@SerializedName("category") val category : String,
	@SerializedName("subcategory") val subcategory : String
)