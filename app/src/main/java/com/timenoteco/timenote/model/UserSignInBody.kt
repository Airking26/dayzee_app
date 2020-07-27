package com.timenoteco.timenote.model

import com.google.gson.annotations.SerializedName

data class UserSignInBody(
    @SerializedName("email") val email : String,
    @SerializedName("password") val password : String
)