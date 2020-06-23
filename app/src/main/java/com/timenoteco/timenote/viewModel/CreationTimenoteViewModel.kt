package com.timenoteco.timenote.viewModel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.timenoteco.timenote.model.CreateTimenote
import com.timenoteco.timenote.model.Timenote
import com.timenoteco.timenote.repository.CreationTimenoteData
import java.text.SimpleDateFormat
import java.util.*

class CreationTimenoteViewModel: ViewModel() {

    private val DATE_FORMAT_SAME_DAY_SAME_TIME = "EEE, d MMM yyyy hh:mm aaa"
    private val DATE_FORMAT_SAME_DAY_DIFFERENT_TIME = "EEE, d MMM yyyy hh:mm aaa"
    private val DATE_FORMAT_DIFFERENT_DAY_DIFFERENT_TIME = "EEE, d MMM yyyy hh:mm aaa"
    private val DATE_FORMAT_DIFFERENT_DAY_SAME_TIME = "EEE, d MMM yyyy hh:mm aaa"
    private val YEAR = "yyyy"

    private val timenoteLiveData = MutableLiveData<CreateTimenote>()
    private val createTimenoteData: CreationTimenoteData = CreationTimenoteData()

    fun getCreateTimeNoteLiveData(): LiveData<CreateTimenote>{
        timenoteLiveData.postValue(createTimenoteData.loadCreateTimenoteData())
        return timenoteLiveData
    }

    fun setDescription(description : String){
        timenoteLiveData.postValue(createTimenoteData.setDescription(description))
    }

    fun setPicUser(picUser: Bitmap){
        timenoteLiveData.postValue(createTimenoteData.setPicUser(picUser))
    }

    fun setLocation(location: String){
        timenoteLiveData.postValue(createTimenoteData.setPlace(location))
    }

    fun setYear(year: Long){
        timenoteLiveData.postValue(createTimenoteData.setYear(formatDate(YEAR, year)))
    }

    private fun formatDate(format: String, timestamp: Long): String {
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        return dateFormat.format(timestamp)
    }




}