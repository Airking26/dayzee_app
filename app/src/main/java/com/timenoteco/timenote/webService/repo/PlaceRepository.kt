package com.timenoteco.timenote.webService.repo

import com.timenoteco.timenote.webService.service.PlaceService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PlaceRepository {

    companion object{
       private const val BASE_URL = "https://maps.googleapis.com/maps/api/place/"
    }

    private val service = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getPlaceService(): PlaceService {
        return service.create(PlaceService::class.java)
    }
}