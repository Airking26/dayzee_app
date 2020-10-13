package com.timenoteco.timenote.webService.service

import com.timenoteco.timenote.model.NearbyRequestBody
import com.timenoteco.timenote.model.TimenoteInfoDTO
import retrofit2.Response
import retrofit2.http.*

interface NearbyService {

    @POST("nearby/{offset}")
    suspend fun getNearbyResults(@Header("Authorization") token: String,@Path("offset") offset: Int, @Body nearbyRequestBody: NearbyRequestBody) : Response<List<TimenoteInfoDTO>>
}