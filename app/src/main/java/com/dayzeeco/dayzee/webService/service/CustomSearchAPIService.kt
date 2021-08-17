package com.dayzeeco.dayzee.webService.service

import android.content.Context
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.customsearch.Customsearch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.IOException

class CustomSearchAPIService{

    enum class Result {
        SUCCESS,
        FAILURE
    }

    var firstItemID: Long = 1
    var isType = false
    private var context: Context? = null
    private val images: MutableList<String> = mutableListOf()

    fun setContext(context: Context?) {
        this.context = context
    }

    fun getImages(): MutableList<String> {
        return images
    }

    fun getImagesFromNet(query: String, apiKey: String): Result {
        val customSearch = Customsearch.Builder(NetHttpTransport(), JacksonFactory(), null)
        customSearch.applicationName = "Search"
        try {
            val list = customSearch.build().cse().list(query)
            list.key = apiKey
            list.cx = "018194552039993531144:aj_el4m5plw"
            list.start = firstItemID
            if (isType) list.searchType = "image"
            val results = list.execute()
            if (results.items != null) if (isType) {
                for (res in results.items) {
                    if (res != null) {
                        images.add(res.link)
                    }
                }
            }
            return Result.SUCCESS
        } catch (e: IOException) {
            e.printStackTrace()
            return Result.FAILURE
        }
    }
}