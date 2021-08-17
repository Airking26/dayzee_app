package com.dayzeeco.dayzee.webService.service

import com.dayzeeco.dayzee.model.CommentInfoDTO
import com.dayzeeco.dayzee.model.NotificationInfoDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface NotificationService {

    @GET("notification/{id}/notifications/{offset}")
    suspend fun getNotifications(@Header("Authorization") token: String, @Path("id") id: String, @Path("offset") offset: Int) : Response<List<NotificationInfoDTO>>

    @POST("notification/{id}/")
    suspend fun deleteNotification(@Header("Authorization") token: String, @Path("id") id: String): Response<Any>
}