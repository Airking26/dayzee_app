package com.timenoteco.timenote.model

import com.google.gson.annotations.SerializedName

class PasswordChanged (
    @SerializedName("changed") val changed: Boolean
)