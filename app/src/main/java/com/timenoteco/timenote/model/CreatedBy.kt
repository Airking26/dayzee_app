package com.timenoteco.timenote.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CreatedBy (

	@SerializedName("id") val id : String,
	@SerializedName("email") val email : String,
	@SerializedName("userName") val userName : String,
	@SerializedName("picture") val pictureURL : String,
	@SerializedName("givenName") val givenName : String,
	@SerializedName("familyName") val familyName : String,
	@SerializedName("createdAt") val createdAt : String
) : Parcelable