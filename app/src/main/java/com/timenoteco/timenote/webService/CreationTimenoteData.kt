package com.timenoteco.timenote.webService

import android.graphics.Bitmap
import com.timenoteco.timenote.model.*

private var timenoteModel: CreationTimenoteDTO =
    CreationTimenoteDTO(createdBy = "", title = "", location = Location(0.0, 0.0, Address("", "", "", ""))
        , startingAt = "", endingAt = "", price = 0)

class CreationTimenoteData {


    fun loadCreateTimenoteData(): CreationTimenoteDTO? {
        return timenoteModel
    }

    fun setCreatedBy(id: String): CreationTimenoteDTO{
        timenoteModel.createdBy = id
        return timenoteModel
    }

    fun setPic(pic: List<String>): CreationTimenoteDTO {
        timenoteModel.pictures = pic
        return timenoteModel
    }

    fun setPrice(price: Int): CreationTimenoteDTO{
        timenoteModel.price = price
        return timenoteModel
    }

    fun setUrl(url: String): CreationTimenoteDTO{
        timenoteModel.url = url
        return timenoteModel
    }

    fun setPlace(location: Location): CreationTimenoteDTO {
        timenoteModel.location = location
        return timenoteModel
    }

    fun setTtile(title: String): CreationTimenoteDTO{
        timenoteModel.title = title
        return timenoteModel
    }

    fun setDescription(description: String): CreationTimenoteDTO{
        timenoteModel.description = description
        return timenoteModel
    }

    fun setCategory(category: Category): CreationTimenoteDTO{
        timenoteModel.category = category
        return timenoteModel
    }

    fun setStartDate(startDate: String): CreationTimenoteDTO{
        timenoteModel.startingAt = startDate
        return timenoteModel
    }

    fun setEndDate(endDate: String): CreationTimenoteDTO{
        timenoteModel.endingAt = endDate
        return timenoteModel
    }

    fun clear(): CreationTimenoteDTO {
        CreationTimenoteDTO(createdBy = "", title = "", location = Location(0.0, 0.0, Address("", "", "", ""))
            , startingAt = "", endingAt = "", price = 0)
        return timenoteModel
    }

    fun setColor(color: String): CreationTimenoteDTO {
        timenoteModel.colorHex = color
        return timenoteModel
    }

}