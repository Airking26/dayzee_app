package com.timenoteco.timenote.model

import com.google.gson.annotations.SerializedName
import com.timenoteco.timenote.model.Northeast
import com.timenoteco.timenote.model.Southwest


data class Viewport (

    @SerializedName("northeast") val northeast : Northeast,
    @SerializedName("southwest") val southwest : Southwest
)