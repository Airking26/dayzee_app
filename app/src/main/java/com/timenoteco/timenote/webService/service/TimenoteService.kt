package com.timenoteco.timenote.webService.service

import com.timenoteco.timenote.model.TimenoteModel
import com.timenoteco.timenote.model.UserResponse
import retrofit2.Response
import retrofit2.http.*

interface TimenoteService {

    @GET("timenote/all/{offset}")
    suspend fun getAllTimenotes(@Path ("offset") offset: Int) : Response<List<TimenoteModel>>

    @GET("timenote/{id}")
    suspend fun getTimenoteId(@Path("id") id: String) : Response<TimenoteModel>

    @PATCH("timenote/{id}")
    suspend fun modifyTimenote(@Path("id") id: String, @Body timenote: TimenoteModel) : Response<TimenoteModel>

    @DELETE("timenote/{id}")
    suspend fun deleteTimenote(@Path("id") id: String) : Response<Any>

    @POST("timenote")
    suspend fun createTimenote(@Body timenoteModel: TimenoteModel) : Response<TimenoteModel>

    @PUT("timenote/join/{id}")
    suspend fun joinTimenote(@Path("id") id: String) : Response<Any>

    @PUT("timenote/leave/{id}")
    suspend fun leaveTimenote(@Path("id") id: String) : Response<Any>

    @PUT("timenote/joinPrivate/{id}")
    suspend fun joinPrivateTimenote(@Path("id") id: String) : Response<Any>

    @GET("timenote/{id}/users/{offset}")
    suspend fun getUsersParticipatingTimenote(@Path("id") id: String, @Path("offset") offset: Int) : Response<List<UserResponse>>


}