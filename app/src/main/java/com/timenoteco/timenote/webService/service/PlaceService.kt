package com.timenoteco.timenote.webService.service

import com.timenoteco.timenote.model.DetailedPlace
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaceService {

    @GET("details/json")
    suspend fun getDetailedPlace(@Query("place_id") id : String, @Query("key") key: String) : Response<DetailedPlace>

}