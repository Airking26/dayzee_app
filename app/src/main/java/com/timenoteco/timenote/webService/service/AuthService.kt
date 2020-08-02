package com.timenoteco.timenote.webService.service

import com.timenoteco.timenote.model.RootUserResponse
import com.timenoteco.timenote.model.UserSignInBody
import com.timenoteco.timenote.model.UserSignUpBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthService {

    @POST("auth/signUp")
    suspend fun signUp(@Body userSignUpBody: UserSignUpBody?): Response<RootUserResponse>

    @POST("auth/signIn")
    suspend fun signIn(@Body userSignInBody: UserSignInBody): Response<RootUserResponse>

}