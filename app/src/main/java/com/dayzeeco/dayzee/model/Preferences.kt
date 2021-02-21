package com.dayzeeco.dayzee.model

import com.google.gson.annotations.SerializedName

data class Preferences (
	@SerializedName("category") val category : MutableList<SubCategoryRated>
)