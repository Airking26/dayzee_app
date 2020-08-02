package com.timenoteco.timenote.webService.service

import com.timenoteco.timenote.model.NearbyRequestBody
import com.timenoteco.timenote.model.TimenoteModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET

interface NearbyService {

    @GET("nearby/{offset}")
    suspend fun getNearbyResults(@Body nearbyRequestBody: NearbyRequestBody) : Response<List<TimenoteModel>>
}