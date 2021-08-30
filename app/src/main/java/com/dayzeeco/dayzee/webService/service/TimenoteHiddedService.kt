package com.dayzeeco.dayzee.webService.service

import com.dayzeeco.dayzee.model.HiddedTimenoteInfoDTO
import com.dayzeeco.dayzee.model.TimenoteHiddedCreationDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface TimenoteHiddedService {

    @POST("hidded-timenote-user")
    suspend fun hideEventOrUser(@Header("Authorization") token: String, @Body timenoteHiddedCreationDTO: TimenoteHiddedCreationDTO): Response<HiddedTimenoteInfoDTO>
}