package com.timenoteco.timenote.webService.service

import com.timenoteco.timenote.model.TimenoteModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ProfileService {

    @GET("me/upcoming/{offset}")
    suspend fun getFutureTimenotes(@Header("Authorization") token: String, @Path("offest") offset: Int) : Response<List<TimenoteModel>>

    @GET("me/paste/{offset}")
    suspend fun getPastTimenotes(@Header("Authorization") token: String, @Path("offset") offset: Int) : Response<List<TimenoteModel>>

}