package com.timenoteco.timenote.viewModel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.timenoteco.timenote.model.CreateTimenoteModel
import com.timenoteco.timenote.repository.CreationTimenoteData
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
    private val createTimenoteData: CreationTimenoteData = CreationTimenoteData()

    fun getCreateTimeNoteLiveData(): LiveData<CreateTimenoteModel>{
        timenoteLiveData.postValue(createTimenoteData.loadCreateTimenoteData())
        return timenoteLiveData
    }

    fun setTitle(title : String){
        timenoteLiveData.postValue(createTimenoteData.setTtile(title))
    }

    fun setDescription(description: String){
        timenoteLiveData.postValue(createTimenoteData.setDescription(description))
    }

    fun setPicUser(picUser: Bitmap){
        timenoteLiveData.postValue(createTimenoteData.setPic(picUser))
    }

    fun setLocation(location: String){
        timenoteLiveData.postValue(createTimenoteData.setPlace(location))
    }

    fun setYear(year: Long){
        timenoteLiveData.postValue(createTimenoteData.setYear(formatDate(YEAR, year)))
    }

    fun setCategory(category: String){
        timenoteLiveData.postValue(createTimenoteData.setCategory(category))
    }

    fun setStartDate(startDate: Long){
        timenoteLiveData.postValue(createTimenoteData.setStartDate(formatDate(DATE_FORMAT_SAME_DAY_SAME_TIME, startDate)))
    }

    fun setEndDate(endDate: Long){
        timenoteLiveData.postValue(createTimenoteData.setEndDate(formatDate(DATE_FORMAT_SAME_DAY_SAME_TIME, endDate)))
    }

    fun setColor(color: String){
        timenoteLiveData.postValue(createTimenoteData.setColor(color))
    }

    fun setFormatedStartDate(startDate: Long, endDate: Long){
        if(formatDate(DATE_FORMAT_DAY, startDate) == formatDate(DATE_FORMAT_DAY, endDate)){
            if(formatDate(DATE_FORMAT_TIME, startDate) == formatDate(DATE_FORMAT_TIME, endDate)){
                timenoteLiveData.postValue(createTimenoteData.setFormatedStartDate(formatDate(DATE_FORMAT_TIME_FORMATED, startDate)))
                timenoteLiveData.postValue(createTimenoteData.setFormatedEndDate(formatDate(DATE_FORMAT_TIME, startDate)))
            } else {
                timenoteLiveData.postValue(createTimenoteData.setFormatedStartDate(formatDate(DATE_FORMAT_TIME_FORMATED, startDate)))
                timenoteLiveData.postValue(createTimenoteData.setFormatedEndDate(formatDate(DATE_FORMAT_TIME, startDate) + "\n" + formatDate(DATE_FORMAT_TIME, endDate)))
            }
        } else {
            timenoteLiveData.postValue(createTimenoteData.setFormatedStartDate(formatDate(DATE_FORMAT_SAME_DAY_DIFFERENT_TIME, startDate)))
            timenoteLiveData.postValue(createTimenoteData.setFormatedEndDate(formatDate(DATE_FORMAT_SAME_DAY_DIFFERENT_TIME, endDate)))
        }
    }

    fun clear(){
        timenoteLiveData.postValue(createTimenoteData.clear())
    }

    private fun formatDate(format: String, timestamp: Long): String {
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        return dateFormat.format(timestamp)
    }




}