package com.dayzeeco.dayzee.webService.service

import com.dayzeeco.dayzee.model.CommentInfoDTO
import com.dayzeeco.dayzee.model.NotificationInfoDTO
import retrofit2.Response
import retrofit2.http.*

interface NotificationService {

    @GET("notification/{id}/notifications/{offset}")
    suspend fun getNotifications(@Header("Authorization") token: String, @Path("id") id: String, @Path("offset") offset: Int) : Response<List<NotificationInfoDTO>>

    @DELETE("notification/{id}/")
    suspend fun deleteNotification(@Header("Authorization") token: String, @Path("id") id: String): Response<NotificationInfoDTO>

    @GET("notification/{id}/unreadNotif")
    suspend fun hasUnreadNotification(@Header("Authorization") token: String, @Path("id") id: String) : Response<Boolean>
}