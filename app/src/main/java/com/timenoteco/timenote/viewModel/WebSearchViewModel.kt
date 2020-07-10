package com.timenoteco.timenote.viewModel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timenoteco.timenote.common.SearchTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL


class WebSearchViewModel: ViewModel(){

    private lateinit var context: Context
    private val searchTask = SearchTask()
    private var results: MutableList<String> = mutableListOf()
    private var bitmap: Bitmap? = null
    private val searchWebLiveData = MutableLiveData<MutableList<String>>()
    private val transformImageLiveData = MutableLiveData<Bitmap>()

    fun getListResults(): LiveData<MutableList<String>>{
        searchWebLiveData.postValue(results)
        return searchWebLiveData
    }

    fun getBitmap(): LiveData<Bitmap>{
        transformImageLiveData.postValue(bitmap)
        return transformImageLiveData
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

    fun decodeSampledBitmapFromResource (url: URL, resId: Rect?, reqWidth: Int, reqHeight: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            try {
                BitmapFactory.decodeStream(url.openConnection().getInputStream(), resId, options)
                //options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
                options.inJustDecodeBounds = false
                transformImageLiveData.postValue(BitmapFactory.decodeStream(url.openConnection().getInputStream(), resId, options)!!)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    }


}