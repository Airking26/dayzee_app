package com.timenoteco.timenote.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.timenoteco.timenote.model.Category
import com.timenoteco.timenote.model.CreatedBy
import com.timenoteco.timenote.model.Location
import kotlinx.android.parcel.Parcelize


@Parcelize
data class TimenoteInfoDTO (
	@SerializedName("id") val id : String,
	@SerializedName("createdAt") val createdAt : String,
	@SerializedName("createdBy") val createdBy : UserInfoDTO,
	@SerializedName("organizers") val organizers : List<UserInfoDTO>?,
	@SerializedName("title") val title : String,
	@SerializedName("description") val description : String?,
	@SerializedName("pictures") val pictures : List<String>?,
	@SerializedName("colorHex") val colorHex : String?,
	@SerializedName("location") val location : Location?,
	@SerializedName("category") val category : Category?,
	@SerializedName("startingAt") val startingAt : String,
	@SerializedName("endingAt") val endingAt : String,
	@SerializedName("hashtags") val hashtags : List<String>?,
	@SerializedName("url") val url : String?,
	@SerializedName("price") val price : Price,
	@SerializedName("likedBy") val likedBy : Int?,
	@SerializedName("joinedBy") val joinedBy : JoinedBy?,
	@SerializedName("comments") val commentAccount: Int?,
	@SerializedName("participating") val isParticipating: Boolean
) : Parcelable