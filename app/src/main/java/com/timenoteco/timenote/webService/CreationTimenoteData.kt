package com.timenoteco.timenote.webService

import android.graphics.Bitmap
import com.timenoteco.timenote.model.*

private var timenoteModel: CreateTimenoteModel =
    CreateTimenoteModel(null, null, null, null, null, null,
        null, null, null, null, null, null, null, null)

class CreationTimenoteData {

    fun loadCreateTimenoteData(): CreateTimenoteModel? {
        return timenoteModel
    }

    fun setPic(pic: MutableList<Bitmap>): CreateTimenoteModel {
        timenoteModel.pic = pic
        return timenoteModel
    }

    fun setPrice(price: Long): CreateTimenoteModel{
        timenoteModel.price = price
        return timenoteModel
    }

    fun setUrl(url: String): CreateTimenoteModel{
        timenoteModel.url = url
        return timenoteModel
    }

    fun setPlace(location: String): CreateTimenoteModel {
        timenoteModel.place = location
        return timenoteModel
    }

    fun setTtile(title: String): CreateTimenoteModel{
        timenoteModel.title = title
        return timenoteModel
    }

    fun setStatus(StatusTimenote: StatusTimenote): CreateTimenoteModel{
        timenoteModel.status = StatusTimenote
        return timenoteModel
    }

    fun setDescription(description: String): CreateTimenoteModel{
        timenoteModel.desc = description
        return timenoteModel
    }

    fun setYear(year: String): CreateTimenoteModel{
        timenoteModel.year = year
        return timenoteModel
    }

    fun setCategory(category: String): CreateTimenoteModel{
        timenoteModel.category = category
        return timenoteModel
    }

    fun setStartDate(startDate: String): CreateTimenoteModel{
        timenoteModel.startDate = startDate
        return timenoteModel
    }

    fun setEndDate(endDate: String): CreateTimenoteModel{
        timenoteModel.endDate = endDate
        return timenoteModel
    }

    fun setFormatedStartDate(startDate: String): CreateTimenoteModel{
        timenoteModel.formatedStartDate = startDate
        return timenoteModel
    }

    fun setFormatedEndDate(endDate: String): CreateTimenoteModel{
        timenoteModel.formatedEndDate = endDate
        return timenoteModel
    }

    fun clear(): CreateTimenoteModel {
        timenoteModel = CreateTimenoteModel(null, null, null, null, null,
            null, null, null, null, null, null, null, null, null)
        return timenoteModel
    }

    fun setColor(color: String): CreateTimenoteModel {
        timenoteModel.color = color
        return timenoteModel
    }

}