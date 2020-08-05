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

    private val timenoteLiveData = MutableLiveData<CreateTimenoteModel>()
    private val timenoteLiveDataDB = MutableLiveData<CreateTimenoteModelDB>()
    private val createTimenoteData: CreationTimenoteData = CreationTimenoteData()
    private val placeService = PlaceRepository().getPlaceService()
    private val timenoteService = DayzeeRepository().getTimenoteService()

    fun getCreateTimeNoteLiveData(): LiveData<CreateTimenoteModel>{
        timenoteLiveData.postValue(createTimenoteData.loadCreateTimenoteData())
        return timenoteLiveData
    }

    fun getCreateTimeNoteLiveDataDB(): LiveData<CreateTimenoteModelDB>{
        timenoteLiveDataDB.postValue(createTimenoteData.loadCreateTimenoteDataDB())
        return timenoteLiveDataDB
    }

    fun setFormat(format: Int) = timenoteLiveData.postValue(createTimenoteData.setFormat(format))
    fun setTitle(title : String) = timenoteLiveData.postValue(createTimenoteData.setTtile(title))
    fun setPrice(price: Long) = timenoteLiveData.postValue(createTimenoteData.setPrice(price))
    fun setUrl(url: String) = timenoteLiveData.postValue(createTimenoteData.setUrl(url))
    fun setDescription(description: String) = timenoteLiveData.postValue(createTimenoteData.setDescription(description))
    fun setPicUser(pic: MutableList<Bitmap>) = timenoteLiveData.postValue(createTimenoteData.setPic(pic))
    fun setLocation(location: String) = timenoteLiveData.postValue(createTimenoteData.setPlace(location))
    fun setYear(year: Long) = timenoteLiveData.postValue(createTimenoteData.setYear(formatDate(YEAR, year)))
    fun setCategory(category: String) = timenoteLiveData.postValue(createTimenoteData.setCategory(category))
    fun setStartDate(startDate: Long, format: String) = timenoteLiveData.postValue(createTimenoteData.setStartDate(formatDate(format, startDate)))
    fun setEndDate(endDate: Long) = timenoteLiveData.postValue(createTimenoteData.setEndDate(formatDate(DATE_FORMAT_SAME_DAY_SAME_TIME, endDate)))
    fun setColor(color: String) = timenoteLiveData.postValue(createTimenoteData.setColor(color))
    fun setStatus(StatusTimenote: StatusTimenote) = timenoteLiveData.postValue(createTimenoteData.setStatus(StatusTimenote))
    fun fetchLocation(id : String): LiveData<Response<DetailedPlace>> {
      return flow {
          emit(placeService.getDetailedPlace(id, "AIzaSyBhM9HQo1fzDlwkIVqobfmrRmEMCWTU1CA"))
      }.asLiveData(viewModelScope.coroutineContext)
    }

    fun setLocationObject(detailedPlace: DetailedPlace){
        timenoteLiveDataDB.postValue(createTimenoteData.setLocation(detailedPlace))
    }

    fun setFormatedStartDate(startDate: Long, endDate: Long){
        if(formatDate(DATE_FORMAT_DAY, startDate) == formatDate(DATE_FORMAT_DAY, endDate)){
            if(formatDate(DATE_FORMAT_TIME, startDate) == formatDate(DATE_FORMAT_TIME, endDate)){
                timenoteLiveData.postValue(createTimenoteData.setFormatedStartDate(formatDate(DATE_FORMAT_TIME_FORMATED, startDate)))
                timenoteLiveData.postValue(createTimenoteData.setFormatedEndDate(formatDate(DATE_FORMAT_TIME, startDate)))
                timenoteLiveData.postValue(createTimenoteData.setFormat(0))
            } else {
                timenoteLiveData.postValue(createTimenoteData.setFormatedStartDate(formatDate(DATE_FORMAT_TIME_FORMATED, startDate)))
                timenoteLiveData.postValue(createTimenoteData.setFormatedEndDate(formatDate(DATE_FORMAT_TIME, startDate) + "\n" + formatDate(DATE_FORMAT_TIME, endDate)))
                timenoteLiveData.postValue(createTimenoteData.setFormat(0))
            }
        } else {
            timenoteLiveData.postValue(createTimenoteData.setFormatedStartDate(formatDate(DATE_FORMAT_SAME_DAY_DIFFERENT_TIME, startDate)))
            timenoteLiveData.postValue(createTimenoteData.setFormatedEndDate(formatDate(DATE_FORMAT_SAME_DAY_DIFFERENT_TIME, endDate)))
            timenoteLiveData.postValue(createTimenoteData.setFormat(1))
        }
    }

    fun clear() = timenoteLiveData.postValue(createTimenoteData.clear())


    private fun formatDate(format: String, timestamp: Long): String {
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        return if(timestamp == 0L) ""
        else dateFormat.format(timestamp)
    }

    fun postTimenote(): LiveData<Response<TimenoteModel>> {
        return flow {
            emit(timenoteService.createTimenote(timenoteLiveData.value!!)
            )
        }.asLiveData(viewModelScope.coroutineContext)
    }



}