package com.timenoteco.timenote.webService.service

import com.timenoteco.timenote.model.*
import retrofit2.Response
import retrofit2.http.*

interface ProfileService {

    @GET("profile/{id}/upcoming/{offset}")
    suspend fun getFutureTimenotes(@Header("Authorization") token: String,@Path("id") id: String, @Path("offset") offset: Int) : Response<List<TimenoteInfoDTO>>

    @GET("profile/{id}/past/{offset}")
    suspend fun getPastTimenotes(@Header("Authorization") token: String,@Path("id") id: String, @Path("offset") offset: Int) : Response<List<TimenoteInfoDTO>>

    @POST("profile/group")
    suspend fun createGroup(@Header("Authorization") token: String, @Body createGroupDTO: CreateGroupDTO) : Response<UserGroupDTO>

    @PATCH("profile/group/{id}")
    suspend fun modifyGroup(@Header("Authorization") token: String, @Path("id") id: String, @Body createGroupDTO: CreateGroupDTO) : Response<UserGroupDTO>

    @DELETE("profile/group/{id}")
    suspend fun deleteGroup(@Header("Authorization") token: String, @Path("id") id: String) : Response<UserGroupDTO>

    @GET("profile/groups")
    suspend fun getAllGroups(@Header("Authorization") token: String) : Response<UserGroupDTO>

    @POST("profile/timenotes/filtered/{offset}")
    suspend fun getTimenotesFiltered(@Header("Authorization") token: String, @Path("offset") offset: Int, @Body timenoteFilteredDTO: TimenoteFilteredDTO) : Response<List<TimenoteInfoDTO>>

    @POST("profile/timenotes/date")
    suspend fun getTimenoteByDate(@Header("Authorization") token: String, @Body timenoteDateFilteredDTO: TimenoteDateFilteredDTO) : Response<List<TimenoteInfoDTO>>

}