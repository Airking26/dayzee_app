package com.timenoteco.timenote.viewModel

import android.graphics.Bitmap
import androidx.lifecycle.*
import com.timenoteco.timenote.model.*
import com.timenoteco.timenote.webService.CreationTimenoteData
import com.timenoteco.timenote.webService.repo.DayzeeRepository
import com.timenoteco.timenote.webService.repo.PlaceRepository
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class CreationTimenoteViewModel: ViewModel() {

    private val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

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
    fun setDescription(description: String) = timenoteLiveData.postValue(createTimenoteData.setDescription(description))
    fun setPicUser(pic: List<String>) = timenoteLiveData.postValue(createTimenoteData.setPic(pic))
    fun setLocation(location: Location) = timenoteLiveData.postValue(createTimenoteData.setPlace(location))
    fun setCategory(category: Category) = timenoteLiveData.postValue(createTimenoteData.setCategory(category))
    fun setStartDate(startDate: Long, format: String) = timenoteLiveData.postValue(createTimenoteData.setStartDate(formatDate(format, startDate)))
    fun setEndDate(endDate: Long) = timenoteLiveData.postValue(createTimenoteData.setEndDate(formatDate(ISO, endDate)))
    fun setColor(color: String) = timenoteLiveData.postValue(createTimenoteData.setColor(color))
    fun setCreatedBy(id: String) = timenoteLiveData.postValue(createTimenoteData.setCreatedBy(id))
    fun fetchLocation(id : String): LiveData<Response<DetailedPlace>> {
        return flow {
            emit(placeService.getDetailedPlace(id, "AIzaSyBhM9HQo1fzDlwkIVqobfmrRmEMCWTU1CA"))
        }.asLiveData(viewModelScope.coroutineContext)
    }

    fun clear() = timenoteLiveData.postValue(createTimenoteData.clear())


    private fun formatDate(format: String, timestamp: Long): String {
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        return if(timestamp == 0L) ""
        else dateFormat.format(timestamp)
    }

}