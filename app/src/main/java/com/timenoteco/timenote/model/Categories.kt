package com.timenoteco.timenote.model

import com.google.gson.annotations.SerializedName
data class Categories (

	@SerializedName("category") val category : String,
	@SerializedName("subcategory") val subcategory : String
)