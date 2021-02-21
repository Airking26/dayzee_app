package com.dayzeeco.dayzee.model

import com.google.gson.annotations.SerializedName

data class UserNameSignInBody(
    @SerializedName("userName") val username : String,
    @SerializedName("password") val password : String
)