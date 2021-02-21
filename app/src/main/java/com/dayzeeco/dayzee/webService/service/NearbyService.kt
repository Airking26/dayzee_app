package com.dayzeeco.dayzee.webService.service

import com.dayzeeco.dayzee.model.NearbyRequestBody
import com.dayzeeco.dayzee.model.TimenoteInfoDTO
import retrofit2.Response
import retrofit2.http.*

interface NearbyService {

    @POST("nearby/{offset}")
    suspend fun getNearbyResults(@Path("offset") offset: Int, @Body nearbyRequestBody: NearbyRequestBody) : Response<List<TimenoteInfoDTO>>
}