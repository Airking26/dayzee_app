package com.dayzeeco.dayzee.viewModel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dayzeeco.dayzee.webService.service.CustomSearchAPIService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.URL


class WebSearchViewModel: ViewModel(){

    private lateinit var context: Context
    private val searchTask =
        CustomSearchAPIService()
    private var results: MutableList<String> = mutableListOf()
    private var bitmap: Bitmap? = null
    private val searchWebLiveData = MutableLiveData<MutableList<String>>()
    private val transformImageLiveData = MutableLiveData<Bitmap?>()

    fun getListResults(): LiveData<MutableList<String>>{
        searchWebLiveData.postValue(results)
        return searchWebLiveData
    }

    fun search(keyword: String, context: Context, id: Long, apiKey: String){
        extractImages(keyword, context, id, apiKey)
        this.context = context
    }

    fun getBitmap(): MutableLiveData<Bitmap?> {
        if(bitmap != null) transformImageLiveData.postValue(bitmap!!)
        return transformImageLiveData
    }

    fun clearBitmap(){
        transformImageLiveData.postValue(null)
    }

    private fun extractImages(searchQuery: String, context: Context, id: Long, apiKey: String){
        searchTask.firstItemID = id
        searchTask.isType = true
        searchTask.setContext(context)
        viewModelScope.launch(Dispatchers.IO) {
            when(searchTask.getImagesFromNet(searchQuery, apiKey)){
                    CustomSearchAPIService.Result.SUCCESS -> {
                        results = searchTask.getImages()
                        searchWebLiveData.postValue(results)
                    }
            }
        }
    }

    fun decodeSampledBitmapFromResource (url: URL, resId: Rect?, reqWidth: Int, reqHeight: Int){
        viewModelScope.launch(Dispatchers.IO) {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            try {
                BitmapFactory.decodeStream(url.openConnection().getInputStream(), resId, options)
                options.inJustDecodeBounds = false
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream(), resId, options)
                transformImageLiveData.postValue(bitmap)
                bitmap = null
                //transformImageLiveData.postValue(BitmapFactory.decodeStream(url.openConnection().getInputStream(), resId, options)!!)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


}