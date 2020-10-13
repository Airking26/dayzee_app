package com.timenoteco.timenote.model

import com.google.gson.annotations.SerializedName

data class UserGroupDTO (
	@SerializedName("id") val id : String,
	@SerializedName("name") val name : String,
	@SerializedName("users") val users : List<UserInfoDTO>
)