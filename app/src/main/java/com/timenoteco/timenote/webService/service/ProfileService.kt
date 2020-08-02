package com.timenoteco.timenote.webService.service

import com.timenoteco.timenote.model.Json4Kotlin_Base
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ProfileService {

    @GET("me/created/upcoming/{offset}")
    suspend fun getFutureTimenotesCreatedByMe(@Path("offest") offset: Int) : Response<List<Json4Kotlin_Base>>

    @GET("me/created/paste/{offset}")
    suspend fun getPastTimenoteCreatedByMe(@Path("offset") offset: Int) : Response<List<Json4Kotlin_Base>>

    @GET("me/joined/upcoming/{offset}")
    suspend fun getFutureTimenotesJoined(@Path("offset") offset: Int) : Response<List<Json4Kotlin_Base>>

    @GET("me/joined/past/{offset}")
    suspend fun getPastTimenotesJoined(@Path("offset") offset: Int) : Response<List<Json4Kotlin_Base>>
}