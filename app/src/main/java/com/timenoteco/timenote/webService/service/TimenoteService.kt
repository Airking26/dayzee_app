package com.timenoteco.timenote.webService.service

import com.timenoteco.timenote.model.Json4Kotlin_Base
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface TimenoteService {

    @GET("timenote/all/{offset}")
    suspend fun getAllTimenotes(@Path ("offset") offset: Int) : Response<List<Json4Kotlin_Base>>

}