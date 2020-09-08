package com.timenoteco.timenote.webService.service

import com.timenoteco.timenote.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthService {

    @POST("auth/signUp")
    suspend fun signUp(@Body userSignUpBody: UserSignUpBody?): Response<RootUserResponse>

    @POST("auth/signIn/email")
    suspend fun signInEmail(@Body userEmailSignInBody: UserEmailSignInBody): Response<RootUserResponse>

    @POST("auth/signIn/userName")
    suspend fun signInUsername(@Body userNameSignInBody: UserNameSignInBody): Response<RootUserResponse>

    @GET("auth/availability/email/{email}")
    suspend fun checkEmailAvailability(@Path("email") email: String): Response<IsAvailable>

    @GET("auth/availability/userName/{userName}")
    suspend fun checkUsernameAvailability(@Path("userName") username: String): Response<IsAvailable>

}