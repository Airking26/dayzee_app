package com.dayzeeco.dayzee.webService

import android.graphics.Bitmap
import com.dayzeeco.dayzee.model.*

private var timenoteModel: CreationTimenoteDTO = CreationTimenoteDTO(createdBy = "", title = "", startingAt = "", endingAt = "", price = Price(0, ""))

class CreationTimenoteData {

    fun loadCreateTimenoteData(): CreationTimenoteDTO? {
        return timenoteModel
    }

    fun setDuplicateOrEdit(creationTimenoteDTO: CreationTimenoteDTO): CreationTimenoteDTO{
        timenoteModel = creationTimenoteDTO
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

    fun setPrice(price: Price): CreationTimenoteDTO{
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

    fun setHashtags(hashtags: List<String>): CreationTimenoteDTO{
        timenoteModel.hashtags = hashtags
        return timenoteModel
    }

    fun setDescription(description: String): CreationTimenoteDTO{
        timenoteModel.description = description
        return timenoteModel
    }

    fun setCategory(category: Category?): CreationTimenoteDTO{
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

    fun setOrganizers(organizers: List<String>): CreationTimenoteDTO{
        timenoteModel.organizers = organizers
        return timenoteModel
    }

    fun setSharedWith(listSharedWith: List<String>): CreationTimenoteDTO{
        timenoteModel.sharedWith = listSharedWith
        return timenoteModel
    }

    fun setUrlTitle(urlTitle: String?) : CreationTimenoteDTO{
        timenoteModel.urlTitle = urlTitle
        return timenoteModel
    }

    fun clear(): CreationTimenoteDTO {
        timenoteModel = CreationTimenoteDTO(createdBy = "", title = "", startingAt = "", endingAt = "", price = Price(0, ""), pictures = listOf(), hashtags = listOf(), description = "", category = Category("", ""), colorHex = "")
        return timenoteModel
    }

    fun setColor(color: String): CreationTimenoteDTO {
        timenoteModel.colorHex = color
        return timenoteModel
    }

}