package com.timenoteco.timenote.repository

import com.timenoteco.timenote.model.Timenote

private var timenote: Timenote =
    Timenote(null, null, null, null, null, null,
        null, null, null, null, null, null, null)

class CreationTimenoteData {

    fun setDescription(description: String): Timenote{
        timenote.title = description
        return timenote
    }

}