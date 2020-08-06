package com.timenoteco.timenote.model

import com.google.gson.annotations.SerializedName

data class Preferences (
	@SerializedName("category") val category : MutableList<SubCategoryRated>
)