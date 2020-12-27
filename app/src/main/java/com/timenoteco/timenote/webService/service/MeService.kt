package com.timenoteco.timenote.webService.service

import com.timenoteco.timenote.model.FCMDTO
import com.timenoteco.timenote.model.UpdateUserInfoDTO
import com.timenoteco.timenote.model.UserInfoDTO
import retrofit2.Response
import retrofit2.http.*

interface MeService{

    @GET("users/me")
    suspend fun getMyInfos(@Header("Authorization") token: String) : Response<UserInfoDTO>

    @PATCH("users/me")
    suspend fun modifyMyInfos(
        @Header("Authorization") token: String,
        @Body updateUserInfo: UpdateUserInfoDTO
    ) : Response<UserInfoDTO>

    @DELETE("users/me")
    suspend fun deleteMe(@Header("Authorization") token: String) : Response<UserInfoDTO>

    @GET("users/all")
    suspend fun getAllUsers(@Header("Authorization") token: String) : Response<List<UserInfoDTO>>

    @PUT("users/me/fcm")
    suspend fun putFCMToken(@Header("Authorization") token: String, @Body fcmToken : FCMDTO) : Response<UserInfoDTO>

    @GET("users/{id}")
    suspend fun getSpecificUser(@Header("Authorization") token: String, @Path("id") id: String) : Response<UserInfoDTO>

    @PATCH("users/changePassword/{password}")
    suspend fun changePassword(@Header("Authorization") token: String, @Path("password") password : String) : Response<UserInfoDTO>

}