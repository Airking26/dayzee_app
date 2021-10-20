package com.dayzeeco.dayzee.webService.service

import com.dayzeeco.dayzee.model.HiddedTimenoteInfoDTO
import com.dayzeeco.dayzee.model.TimenoteHiddedCreationDTO
import com.dayzeeco.dayzee.model.TimenoteInfoDTO
import com.dayzeeco.dayzee.model.UserInfoDTO
import retrofit2.Response
import retrofit2.http.*

interface TimenoteHiddedService {

    @POST("hidded-timenote-user")
    suspend fun hideEventOrUser(@Header("Authorization") token: String, @Body timenoteHiddedCreationDTO: TimenoteHiddedCreationDTO): Response<HiddedTimenoteInfoDTO>

    @GET("hidded-timenote-user/hidden-users/{offset}")
    suspend fun getHiddenUsers(@Header("Authorization") token: String, @Path("offset") offset: Int): Response<List<UserInfoDTO>>

    @GET("hidded-timenote-user/hidden-events/{offset}")
    suspend fun getHiddenEvents(@Header("Authorization") token: String, @Path("offset") offset: Int): Response<List<TimenoteInfoDTO>>

    @DELETE("hidded-timenote-user/hidden-user/{id}")
    suspend fun removeUserFromHiddens(@Header("Authorization") token: String, @Path("id") id: String): Response<UserInfoDTO>

    @DELETE("hidded-timenote-user/hidden-event/{id}")
    suspend fun removeEventFromHiddens(@Header("Authorization") token: String, @Path("id") id: String): Response<TimenoteInfoDTO>
}