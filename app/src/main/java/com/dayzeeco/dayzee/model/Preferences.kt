package com.dayzeeco.dayzee.model

import com.google.gson.annotations.SerializedName

data class Preferences (
	@SerializedName("preferences") val category : MutableList<SubCategoryRated>
)