package com.timenoteco.timenote.webService

import android.graphics.Bitmap
import com.timenoteco.timenote.model.*


private var timenoteModelDB : CreateTimenoteModelDB =
    CreateTimenoteModelDB("","", "", "",
        Location(123.3, 133.2, Address("", "", "", "")),
        Category("", ""), "", "", listOf(), "", 0, "")

private var timenoteModel: CreateTimenoteModel =
    CreateTimenoteModel(null, null, null, null, null, null,
        null, null, null, null, null, null, null, StatusTimenote.NOANSWER, null)

class CreationTimenoteData {

    fun loadCreateTimenoteData(): CreateTimenoteModel? {
        return timenoteModel
    }

    fun loadCreateTimenoteDataDB(): CreateTimenoteModelDB? {
        return timenoteModelDB
    }

    fun createdBy(id: String): CreateTimenoteModelDB{
        timenoteModelDB.createdBy = id
        return timenoteModelDB
    }

    fun setPic(pic: MutableList<Bitmap>): CreateTimenoteModel {
        timenoteModel.pic = pic
        return timenoteModel
    }

    fun setPrice(price: Long): CreateTimenoteModel{
        timenoteModelDB.price = price.toInt()
        timenoteModel.price = price
        return timenoteModel
    }

    fun setUrl(url: String): CreateTimenoteModel{
        timenoteModelDB.url = url
        timenoteModel.url = url
        return timenoteModel
    }

    fun setPlace(location: String): CreateTimenoteModel {
        timenoteModel.place = location
        return timenoteModel
    }

    fun setTtile(title: String): CreateTimenoteModel{
        timenoteModel.title = title
        timenoteModelDB.title = title
        return timenoteModel
    }

    fun setStatus(StatusTimenote: StatusTimenote): CreateTimenoteModel{
        timenoteModel.status = StatusTimenote
        return timenoteModel
    }

    fun setDescription(description: String): CreateTimenoteModel{
        timenoteModelDB.description = description
        timenoteModel.desc = description
        return timenoteModel
    }

    fun setYear(year: String): CreateTimenoteModel{
        timenoteModel.year = year
        return timenoteModel
    }

    fun setCategory(category: String): CreateTimenoteModel{
        timenoteModelDB.category = Category("", category)
        timenoteModel.category = category
        return timenoteModel
    }

    fun setStartDate(startDate: String): CreateTimenoteModel{
        timenoteModel.startDate = startDate
        timenoteModelDB.startingAt = startDate
        return timenoteModel
    }

    fun setEndDate(endDate: String): CreateTimenoteModel{
        timenoteModel.endDate = endDate
        timenoteModelDB.endingAt = endDate
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

    fun setFormat(format: Int): CreateTimenoteModel{
        timenoteModel.format = format
        return timenoteModel
    }

    fun clear(): CreateTimenoteModel {
        timenoteModel = CreateTimenoteModel(null, null, null, null, null,
            null, null, null, null, null, null, null, null, StatusTimenote.NOANSWER, null)
        return timenoteModel
    }

    fun setColor(color: String): CreateTimenoteModel {
        timenoteModel.color = color
        timenoteModelDB.colorHex = color
        return timenoteModel
    }

    fun setLocation(detailedPlace: DetailedPlace): CreateTimenoteModelDB {
        var zipcode = ""
        var city = ""
        var country = ""
        for(n in detailedPlace.result.address_components){
            if(n.types.contains("locality")) city = n.long_name
            if(n.types.contains("postal_code")) zipcode = n.short_name
            if(n.types.contains("country")) country = n.long_name
        }
        timenoteModelDB.location = Location(detailedPlace.result.geometry.location.lat, detailedPlace.result.geometry.location.lng,
            Address(detailedPlace.result.formatted_address, zipcode, city, country))
        return timenoteModelDB
    }

}