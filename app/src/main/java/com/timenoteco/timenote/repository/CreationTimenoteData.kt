package com.timenoteco.timenote.repository

import android.graphics.Bitmap
import android.net.Uri
import com.timenoteco.timenote.model.CreateTimenoteModel
import com.timenoteco.timenote.model.statusTimenote

private var timenoteModel: CreateTimenoteModel =
    CreateTimenoteModel(null, null, null, null, null, null,
        null, null, null, null, null, null, null, statusTimenote.NOANSWER)

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

    fun setStatus(statusTimenote: statusTimenote): CreateTimenoteModel{
        timenoteModel.status = statusTimenote
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
            null, null, null, null, null, null, null, null, statusTimenote.NOANSWER)
        return timenoteModel
    }

    fun setColor(color: String): CreateTimenoteModel {
        timenoteModel.color = color
        return timenoteModel
    }

}