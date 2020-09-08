package com.timenoteco.timenote.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Address (

	@SerializedName("address") var address : String,
	@SerializedName("zipCode") var zipCode : String,
	@SerializedName("city") var city : String,
	@SerializedName("country") var country : String
) : Parcelable