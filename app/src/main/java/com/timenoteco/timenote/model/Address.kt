package com.timenoteco.timenote.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Address (

	@SerializedName("address") val address : String,
	@SerializedName("zipCode") val zipCode : String,
	@SerializedName("city") val city : String,
	@SerializedName("country") val country : String
) : Parcelable