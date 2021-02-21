package com.dayzeeco.dayzee.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.dayzeeco.dayzee.model.*
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SocialMedias (

	@SerializedName("youtube") val youtube : Youtube,
	@SerializedName("facebook") val facebook : Facebook,
	@SerializedName("instagram") val instagram : Instagram,
	@SerializedName("whatsApp") val whatsApp : WhatsApp,
	@SerializedName("linkedIn") val linkedIn : LinkedIn
) : Parcelable