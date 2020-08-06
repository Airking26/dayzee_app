package com.timenoteco.timenote.webService.service

import com.timenoteco.timenote.model.CreateTimenoteModel
import com.timenoteco.timenote.model.TimenoteBody
import com.timenoteco.timenote.model.TimenoteModel
import com.timenoteco.timenote.model.UserResponse
import retrofit2.Response
import retrofit2.http.*

interface TimenoteService {

    @GET("timenote/all/{offset}")
    suspend fun getAllTimenotes(@Header("Authorization") token: String, @Path ("offset") offset: Int) : Response<List<TimenoteModel>>

    @GET("timenote/{id}")
    suspend fun getTimenoteId(@Header("Authorization") token: String, @Path("id") id: String) : Response<TimenoteModel>

    @PATCH("timenote/{id}")
    suspend fun modifyTimenote(@Header("Authorization") token: String, @Path("id") id: String, @Body timenote: TimenoteBody) : Response<TimenoteModel>

    @DELETE("timenote/{id}")
    suspend fun deleteTimenote(@Header("Authorization") token: String, @Path("id") id: String) : Response<Any>

    @POST("timenote")
    suspend fun createTimenote(@Header("Authorization") token: String, @Body timenoteModel: CreateTimenoteModel) : Response<TimenoteModel>

    @PUT("timenote/join/{id}")
    suspend fun joinTimenote(@Header("Authorization") token: String, @Path("id") id: String) : Response<Any>

    @PUT("timenote/leave/{id}")
    suspend fun leaveTimenote(@Header("Authorization") token: String, @Path("id") id: String) : Response<Any>

    @PUT("timenote/joinPrivate/{id}")
    suspend fun joinPrivateTimenote(@Header("Authorization") token: String, @Path("id") id: String) : Response<Any>

    @GET("timenote/{id}/users/{offset}")
    suspend fun getUsersParticipatingTimenote(@Header("Authorization") token: String, @Path("id") id: String, @Path("offset") offset: Int) : Response<List<UserResponse>>


}