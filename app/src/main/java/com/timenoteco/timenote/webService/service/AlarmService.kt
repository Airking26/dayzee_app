package com.timenoteco.timenote.webService.service

import com.timenoteco.timenote.model.AlarmCreationDTO
import com.timenoteco.timenote.model.AlarmInfoDTO
import retrofit2.Response
import retrofit2.http.*

interface AlarmService {

    @GET("alarm/me")
    suspend fun getAlarms(@Header("Authorization") token: String) : Response<List<AlarmInfoDTO>>

    @POST("alarm/create")
    suspend fun createAlarm(@Header("Authorization") token: String, @Body alarmCreationDTO: AlarmCreationDTO) : Response<AlarmInfoDTO>

    @PATCH("alarm/update/{id}")
    suspend fun updateAlarm(@Header("Authorization") token: String, @Path("id") id: String, @Body alarmCreationDTO: AlarmCreationDTO) : Response<AlarmInfoDTO>

    @DELETE("alarm/delete/{id}")
    suspend fun deleteAlarm(@Header("Authorization") token: String, @Path("id") id: String) : Response<AlarmInfoDTO>

}