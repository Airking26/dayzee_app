package com.timenoteco.timenote.repository

import android.graphics.Bitmap
import com.timenoteco.timenote.model.CreateTimenote
import com.timenoteco.timenote.model.Timenote

private var timenote: CreateTimenote =
    CreateTimenote(null, null, null, null, null, null, null, null)

class CreationTimenoteData {

    fun loadCreateTimenoteData(): CreateTimenote? {
        return timenote
    }

    fun setDescription(description: String): CreateTimenote{
        timenote.title = description
        return timenote
    }

    fun setPicUser(pic: Bitmap): CreateTimenote {
        timenote.pic = pic
        return timenote
    }

    fun setPlace(location: String): CreateTimenote {
        timenote.place = location
        return timenote
    }

    fun setYear(year: String): CreateTimenote{
        timenote.year = year
        return timenote
    }

}