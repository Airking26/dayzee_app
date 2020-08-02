package com.timenoteco.timenote.webService.service

import com.timenoteco.timenote.model.CreateTimenoteModel
import com.timenoteco.timenote.model.Json4Kotlin_Base
import com.timenoteco.timenote.model.TimenoteCreationModel
import retrofit2.Response
import retrofit2.http.*

interface TimenoteService {

    @GET("timenote/all/{offset}")
    suspend fun getAllTimenotes(@Path ("offset") offset: Int) : Response<List<Json4Kotlin_Base>>

    @GET("timenote/{id}")
    suspend fun getTimenoteId(@Path("id") id: String)

    @PATCH("timenote/{id}")
    suspend fun modifyTimenote(@Path("id") id: String, @Body timenote: Json4Kotlin_Base)

    @DELETE("timenote/{id}")
    suspend fun deleteTimenote(@Path("id") id: String)

    @POST("timenote")
    suspend fun createTimenote(@Body timenoteCreationModel: TimenoteCreationModel)

    @PUT("timenote/join/{id}")
    suspend fun joinTimenote(@Path("id") id: String)

    @PUT("timenote/leave/{id}")
    suspend fun leaveTimenote(@Path("id") id: String)

    @PUT("timenote/joinPrivate/{id}")
    suspend fun joinPrivateTimenote(@Path("id") id: String)

    @GET("timenote/{id}/users/{offset}")
    suspend fun getUsersParticipatingTimenote(@Path("id") id: String, @Path("offset") offset: Int)

}