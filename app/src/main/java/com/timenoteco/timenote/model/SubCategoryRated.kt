package com.timenoteco.timenote.model

import com.google.gson.annotations.SerializedName

data class SubCategoryRated (

	@SerializedName("category") val category : Category,
	@SerializedName("rating") var rating : Int
)