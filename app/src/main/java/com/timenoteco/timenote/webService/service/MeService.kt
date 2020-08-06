package com.timenoteco.timenote.webService.service

import com.timenoteco.timenote.model.RootUserResponse
import com.timenoteco.timenote.model.UserResponse
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH

interface MeService{

    @GET("users/me")
    suspend fun getMyInfos(@Header("Authorization") token: String) : Response<UserResponse>

    @PATCH("users/me")
    suspend fun modifyMyInfos(@Header("Authorization") token: String) : Response<UserResponse>

    @DELETE("users/me")
    suspend fun deleteMe(@Header("Authorization") token: String) : Response<UserResponse>

    @GET("users/all")
    suspend fun getAllUsers(@Header("Authorization") token: String) : Response<List<UserResponse>>

}