package com.dayzeeco.dayzee.model

import com.google.gson.annotations.SerializedName
import com.dayzeeco.dayzee.model.Northeast
import com.dayzeeco.dayzee.model.Southwest


data class Viewport (

    @SerializedName("northeast") val northeast : Northeast,
    @SerializedName("southwest") val southwest : Southwest
)