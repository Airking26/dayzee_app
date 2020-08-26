package com.timenoteco.timenote.webService.service

import com.timenoteco.timenote.model.ProfilModifyModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PATCH

interface ProfileModifyService {

    @PATCH("users/me")
    suspend fun modifyProfile (@Header("Authorization") token: String, @Body profilModifyModel: ProfilModifyModel) : Response<Any>
}