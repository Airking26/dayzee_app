package com.timenoteco.timenote.webService.service

import com.timenoteco.timenote.model.TimenoteInfoDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ProfileService {

    @GET("profile/{id}/upcoming/{offset}")
    suspend fun getFutureTimenotes(@Header("Authorization") token: String,@Path("id") id: String, @Path("offset") offset: Int) : Response<List<TimenoteInfoDTO>>

    @GET("me/paste/{offset}")
    suspend fun getPastTimenotes(@Header("Authorization") token: String, @Path("offset") offset: Int) : Response<List<TimenoteInfoDTO>>

}