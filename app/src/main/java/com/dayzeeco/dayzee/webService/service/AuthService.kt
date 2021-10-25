package com.dayzeeco.dayzee.webService.service

import com.dayzeeco.dayzee.model.*
import retrofit2.Response
import retrofit2.http.*

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

    @GET("auth/refresh")
    suspend fun refreshAccessToken(@Header("Refresh") refreshToken: String) : Response<RefreshTokenDTO>

    @GET("auth/resetPassword/{email}")
    suspend fun forgotPassword(@Path("email") email: String) : Response<PasswordChanged>

}