package com.dayzeeco.dayzee.viewModel

import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import androidx.lifecycle.*
import com.dayzeeco.dayzee.model.*
import com.dayzeeco.dayzee.webService.CreationTimenoteData
import com.dayzeeco.dayzee.webService.repo.DayzeeRepository
import com.dayzeeco.dayzee.webService.repo.PlaceRepository
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.abs

class CreationTimenoteViewModel: ViewModel() {

    private val timenoteLiveData = MutableLiveData<CreationTimenoteDTO>()
    private val createTimenoteData: CreationTimenoteData = CreationTimenoteData()
    private val placeService = PlaceRepository().getPlaceService()

    fun getCreateTimeNoteLiveData(): LiveData<CreationTimenoteDTO>{
        timenoteLiveData.postValue(createTimenoteData.loadCreateTimenoteData())
        return timenoteLiveData
    }

    fun setTitle(title : String) = timenoteLiveData.postValue(createTimenoteData.setTtile(title))
    fun setPrice(price: Price) = timenoteLiveData.postValue(createTimenoteData.setPrice(price))
    fun setHashtags(hashtags: List<String>) = timenoteLiveData.postValue(createTimenoteData.setHashtags(hashtags))
    fun setUrl(url: String) = timenoteLiveData.postValue(createTimenoteData.setUrl(url))
    fun setUrlTitle(urlTitle : String) = timenoteLiveData.postValue(createTimenoteData.setUrlTitle(urlTitle))
    fun setDescription(description: String) = timenoteLiveData.postValue(createTimenoteData.setDescription(description))
    fun setPicUser(pic: List<String>) = timenoteLiveData.postValue(createTimenoteData.setPic(pic))
    fun setLocation(location: Location) = timenoteLiveData.postValue(createTimenoteData.setPlace(location))
    fun setCategory(category: Category?) = timenoteLiveData.postValue(createTimenoteData.setCategory(category))
    fun setStartDate(startDate: Long, format: String) = timenoteLiveData.postValue(createTimenoteData.setStartDate(formatDate(format, startDate)))
    fun setStartDateOffset(date: String) = timenoteLiveData.postValue(createTimenoteData.setStartDate(date))
    fun setEndDateOffset(date: String) = timenoteLiveData.postValue(createTimenoteData.setEndDate(date))
    fun setEndDate(endDate: Long, format: String) = timenoteLiveData.postValue(createTimenoteData.setEndDate(formatDate(format, endDate)))
    fun setColor(color: String) = timenoteLiveData.postValue(createTimenoteData.setColor(color))
    fun setCreatedBy(id: String) = timenoteLiveData.postValue(createTimenoteData.setCreatedBy(id))
    fun setOrganizers(organizers: List<String>) = timenoteLiveData.postValue(createTimenoteData.setOrganizers(organizers))
    fun setSharedWith(listSharedWith: List<String>) = timenoteLiveData.postValue(createTimenoteData.setSharedWith(listSharedWith))
    fun setDuplicateOrEdit(creationTimenoteDTO: CreationTimenoteDTO) = timenoteLiveData.postValue(createTimenoteData.setDuplicateOrEdit(creationTimenoteDTO))
    fun fetchLocation(id : String, apiKey: String): LiveData<Response<DetailedPlace>> { return flow { emit(placeService.getDetailedPlace(id, apiKey)) }.asLiveData(viewModelScope.coroutineContext) }

    fun clear() = timenoteLiveData.postValue(createTimenoteData.clear())


    private fun formatDate(format: String, timestamp: Long): String {
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        return if(timestamp == 0L) ""
        else dateFormat.format(timestamp)
    }

    fun setOffset(format: String, date: String, offset: String?): String{
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        return if(offset != null){
            date.replaceRange(date.length - 6, date.length, offset)
        } else formatDate(format, dateFormat.parse(date).time)
    }

}