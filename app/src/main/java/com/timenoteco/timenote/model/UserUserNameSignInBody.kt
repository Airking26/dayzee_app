package com.timenoteco.timenote.model

import com.google.gson.annotations.SerializedName

data class UserUserNameSignInBody(
    @SerializedName("userName") val username : String,
    @SerializedName("password") val password : String
)