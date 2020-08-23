package com.timenoteco.timenote.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Category (
	@SerializedName("category") val category : String,
	@SerializedName("subcategory") val subcategory : String
) : Parcelable