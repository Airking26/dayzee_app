package com.timenoteco.timenote.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.timenoteco.timenote.model.*
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SocialMedias (

	@SerializedName("youtube") val youtube : Youtube,
	@SerializedName("facebook") val facebook : Facebook,
	@SerializedName("instagram") val instagram : Instagram,
	@SerializedName("whatsApp") val whatsApp : WhatsApp,
	@SerializedName("linkedIn") val linkedIn : LinkedIn
) : Parcelable