package com.timenoteco.timenote.webService.service

import com.timenoteco.timenote.model.RootUserResponse
import com.timenoteco.timenote.model.UserResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH

interface MeService{

    @GET("users/me")
    suspend fun getMyInfos() : Response<UserResponse>

    @PATCH("users/me")
    suspend fun modifyMyInfos() : Response<UserResponse>

    @DELETE("users/me")
    suspend fun deleteMe() : Response<UserResponse>

    @GET("users/all")
    suspend fun getAllUsers() : Response<List<UserResponse>>

}