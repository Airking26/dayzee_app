package com.timenoteco.timenote.viewModel

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.timenoteco.timenote.listeners.WebSearcherListener
import com.timenoteco.timenote.common.SearchTask
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException


class WebSearchViewModel: ViewModel(),
    WebSearcherListener {

    private var results: MutableList<Bitmap> = mutableListOf()
    private val searchWebLiveData = MutableLiveData<List<Bitmap>>()

    fun getListResults(): LiveData<List<Bitmap>>{
        searchWebLiveData.postValue(results)
        return searchWebLiveData
    }

    fun search(keyword: String, context: Context){
        extractImages(keyword, context)
    }

    @Throws(IOException::class)
    private fun extractImagesFromGoogle(searchQuery: String): List<String>? {
        //val doc = Jsoup.connect("https://www.google.com/search?tbm=isch&q=$searchQuery").userAgent("Mozilla/5.0 (X11; Linux i686) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.114 Safari/537.36").get()

        //val encodedSearchUrl = "https://www.google.com/search?q=" + URLEncoder.encode(searchQuery, "UTF-8").toString() + "&source=lnms&tbm=isch&sa=X&ved=0ahUKEwiUpP35yNXiAhU1BGMBHdDeBAgQ_AUIECgB"
        val url = "https://www.google.co.in/search?biw=1366&bih=675&tbm=isch&sa=1&ei=qFSJWsuTNc-wzwKFrZHoCw&q=$searchQuery"
        val document: Document = Jsoup.connect(url).get()
        val images = document.getElementsByTag("img")
        //val images = document.select("#rg div.rg_di img")
        val returnedURLS: MutableList<String> = mutableListOf()
        for(i in images){
            returnedURLS.add(i.attr("data-src"))
        }
        return returnedURLS
    }

    private fun extractImages(searchQuery: String, context: Context){
        val searchTask = SearchTask(this)
        searchTask.firstItemID = 0
        searchTask.isType = true
        searchTask.setContext(context)
        searchTask.execute(searchQuery)
    }

    override fun asyncComplete(data: MutableList<Bitmap>?) {
        results = data!!
        searchWebLiveData.postValue(results)
    }


}