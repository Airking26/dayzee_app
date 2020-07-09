package com.timenoteco.timenote.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timenoteco.timenote.common.SearchTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException


class WebSearchViewModel: ViewModel(){

    private lateinit var context: Context
    private val searchTask = SearchTask()
    private var results: MutableList<String> = mutableListOf()
    private val searchWebLiveData = MutableLiveData<List<String>>()

    fun getListResults(): LiveData<List<String>>{
        searchWebLiveData.postValue(results)
        return searchWebLiveData
    }

    fun search(keyword: String, context: Context, id: Long){
        extractImages(keyword, context, id)
        this.context = context
    }

    private fun extractImages(searchQuery: String, context: Context, id: Long){
        searchTask.firstItemID = id
        searchTask.isType = true
        searchTask.setContext(context)
        viewModelScope.launch(Dispatchers.IO) {
                when(searchTask.getImagesFromNet(searchQuery)){
                 SearchTask.Result.SUCCESS -> searchWebLiveData.postValue(searchTask.getImages())
                }
            }
    }


}