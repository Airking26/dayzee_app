package com.timenoteco.timenote.webService.service

import com.timenoteco.timenote.model.*
import retrofit2.Response
import retrofit2.http.*

interface TimenoteService {

    @GET("timenote/feed/upcoming/{offset}")
    suspend fun getUpcomingTimenotes(@Header("Authorization") token: String, @Path ("offset") offset: Int) : Response<List<TimenoteInfoDTO>>

    @GET("timenote/feed/past/{offset}")
    suspend fun getPastTimenotes(@Header("Authorization") token: String, @Path ("offset") offset: Int) : Response<List<TimenoteInfoDTO>>

    @GET("timenote/around/{offset}")
    suspend fun getAroundTimenotes(@Header("Authorization") token: String, @Path ("offset") offset: Int) : Response<List<TimenoteInfoDTO>>

    @GET("timenote/recent/{offset}")
    suspend fun getRecentTimenotes(@Header("Authorization") token: String, @Path ("offset") offset: Int) : Response<List<TimenoteInfoDTO>>

    @GET("timenote/{id}")
    suspend fun getTimenoteId(@Header("Authorization") token: String, @Path("id") id: String) : Response<TimenoteInfoDTO>

    @PATCH("timenote/{id}")
    suspend fun modifyTimenote(@Header("Authorization") token: String, @Path("id") id: String, @Body timenote: CreationTimenoteDTO) : Response<TimenoteInfoDTO>

    @DELETE("timenote/{id}")
    suspend fun deleteTimenote(@Header("Authorization") token: String, @Path("id") id: String) : Response<TimenoteInfoDTO>

    @POST("timenote")
    suspend fun createTimenote(@Header("Authorization") token: String, @Body timenoteModel: CreationTimenoteDTO) : Response<TimenoteInfoDTO>

    @PUT("timenote/join/{id}")
    suspend fun joinTimenote(@Header("Authorization") token: String, @Path("id") id: String) : Response<Any>

    @PUT("timenote/leave/{id}")
    suspend fun leaveTimenote(@Header("Authorization") token: String, @Path("id") id: String) : Response<Any>

    @PUT("timenote/joinPrivate/{id}")
    suspend fun joinPrivateTimenote(@Header("Authorization") token: String, @Path("id") id: String) : Response<Any>

    @GET("timenote/{id}/users/{offset}")
    suspend fun getUsersParticipatingTimenote(@Header("Authorization") token: String, @Path("id") id: String, @Path("offset") offset: Int) : Response<List<UserInfoDTO>>

    @POST("timenote/shareWith")
    suspend fun shareWith(@Header("Authorization") token: String, @Body shareTimenoteDTO: ShareTimenoteDTO) : Response<Any>

    @POST("signalment/timenote")
    suspend fun signaleTimenote(@Header("Authorization") token: String, @Body creationSignalementDTO: TimenoteCreationSignalementDTO) : Response<TimenoteInfoSignalementDTO>
}