package com.timenoteco.timenote.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import java.net.URLEncoder
import java.util.concurrent.Executors

class WebSearchViewModel: ViewModel() {

    private var results: List<String> = listOf()
    private val searchWebLiveData = MutableLiveData<List<String>>()

    fun getListResults(): LiveData<List<String>>{
        searchWebLiveData.postValue(results)
        return searchWebLiveData
    }

    fun search(keyword: String){
        Executors.newSingleThreadExecutor().execute {
            results = extractImagesFromGoogle(keyword)!!
            searchWebLiveData.postValue(results)
        }
        searchWebLiveData.postValue(results)
    }

    @Throws(IOException::class)
    private fun extractImagesFromGoogle(searchQuery: String): List<String>? {
        val encodedSearchUrl =
            "https://www.google.com/search?q=" + URLEncoder.encode(searchQuery, "UTF-8").toString() + "&source=lnms&tbm=isch&sa=X&ved=0ahUKEwiUpP35yNXiAhU1BGMBHdDeBAgQ_AUIECgB"
        val url =
            "https://www.google.co.in/search?biw=1366&bih=675&tbm=isch&sa=1&ei=qFSJWsuTNc-wzwKFrZHoCw&q=$searchQuery"
        val document: Document = Jsoup.connect(encodedSearchUrl).get()
        val images = document.select("img")
        val returnedURLS: MutableList<String> = mutableListOf()
        for(i in images){
            returnedURLS.add(i.attr("data-src"))
        }
        return returnedURLS
    }

}