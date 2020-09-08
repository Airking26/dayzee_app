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

    private val DATE_FORMAT_SAME_DAY_SAME_TIME = "EEE, d MMM yyyy hh:mm aaa"
    private val DATE_FORMAT_DAY = "d MMM yyyy"
    private val DATE_FORMAT_TIME = "hh:mm aaa"
    private val DATE_FORMAT_TIME_FORMATED = "d\nMMM"
    private val DATE_FORMAT_SAME_DAY_DIFFERENT_TIME = "d MMM.\nhh:mm"
    private val YEAR = "yyyy"

    private val timenoteLiveData = MutableLiveData<CreationTimenoteDTO>()
    private val createTimenoteData: CreationTimenoteData = CreationTimenoteData()
    private val placeService = PlaceRepository().getPlaceService()

    fun getCreateTimeNoteLiveData(): LiveData<CreationTimenoteDTO>{
        timenoteLiveData.postValue(createTimenoteData.loadCreateTimenoteData())
        return timenoteLiveData
    }

    fun setTitle(title : String) = timenoteLiveData.postValue(createTimenoteData.setTtile(title))
    fun setPrice(price: Int) = timenoteLiveData.postValue(createTimenoteData.setPrice(price))
    fun setUrl(url: String) = timenoteLiveData.postValue(createTimenoteData.setUrl(url))
    fun setDescription(description: String) = timenoteLiveData.postValue(createTimenoteData.setDescription(description))
    fun setPicUser(pic: List<String>) = timenoteLiveData.postValue(createTimenoteData.setPic(pic))
    fun setLocation(location: Location) = timenoteLiveData.postValue(createTimenoteData.setPlace(location))
    fun setCategory(category: Category) = timenoteLiveData.postValue(createTimenoteData.setCategory(category))
    fun setStartDate(startDate: Long, format: String) = timenoteLiveData.postValue(createTimenoteData.setStartDate(formatDate(format, startDate)))
    fun setEndDate(endDate: Long) = timenoteLiveData.postValue(createTimenoteData.setEndDate(formatDate(DATE_FORMAT_SAME_DAY_SAME_TIME, endDate)))
    fun setColor(color: String) = timenoteLiveData.postValue(createTimenoteData.setColor(color))
    fun fetchLocation(id : String): LiveData<Response<DetailedPlace>> {
        return flow {
            emit(placeService.getDetailedPlace(id, "AIzaSyBhM9HQo1fzDlwkIVqobfmrRmEMCWTU1CA"))
        }.asLiveData(viewModelScope.coroutineContext)
    }

    fun setFormatedStartDate(startDate: Long, endDate: Long){
        if(formatDate(DATE_FORMAT_DAY, startDate) == formatDate(DATE_FORMAT_DAY, endDate)){
            if(formatDate(DATE_FORMAT_TIME, startDate) == formatDate(DATE_FORMAT_TIME, endDate)){
                //timenoteLiveData.postValue(createTimenoteData.setFormatedStartDate(formatDate(DATE_FORMAT_TIME_FORMATED, startDate)))
                //timenoteLiveData.postValue(createTimenoteData.setFormatedEndDate(formatDate(DATE_FORMAT_TIME, startDate)))
            } else {
                //timenoteLiveData.postValue(createTimenoteData.setFormatedStartDate(formatDate(DATE_FORMAT_TIME_FORMATED, startDate)))
                //timenoteLiveData.postValue(createTimenoteData.setFormatedEndDate(formatDate(DATE_FORMAT_TIME, startDate) + "\n" + formatDate(DATE_FORMAT_TIME, endDate)))
            }
        } else {
            //timenoteLiveData.postValue(createTimenoteData.setFormatedStartDate(formatDate(DATE_FORMAT_SAME_DAY_DIFFERENT_TIME, startDate)))
            //timenoteLiveData.postValue(createTimenoteData.setFormatedEndDate(formatDate(DATE_FORMAT_SAME_DAY_DIFFERENT_TIME, endDate)))
        }
    }

    fun clear() = timenoteLiveData.postValue(createTimenoteData.clear())


    private fun formatDate(format: String, timestamp: Long): String {
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        return if(timestamp == 0L) ""
        else dateFormat.format(timestamp)
    }

}