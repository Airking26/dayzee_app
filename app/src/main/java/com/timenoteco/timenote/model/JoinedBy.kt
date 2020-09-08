package com.timenoteco.timenote.model

import com.google.gson.annotations.SerializedName

data class JoinedBy (
    @SerializedName("friends") val users : List<UserInfoDTO>,
    @SerializedName("total") val count : Int
)