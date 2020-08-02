package com.timenoteco.timenote.webService.service

import com.timenoteco.timenote.model.RootUserResponse
import com.timenoteco.timenote.model.UserEmailSignInBody
import com.timenoteco.timenote.model.UserSignUpBody
import com.timenoteco.timenote.model.UserUserNameSignInBody
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
    suspend fun signInUsername(@Body userUserNameSignInBody: UserUserNameSignInBody): Response<RootUserResponse>

    @GET("auth/availability/email/{email}")
    suspend fun checkEmailAvailability(@Path("email") email: String): Boolean

    @GET("auth/availability/userName/{userName}")
    suspend fun checkUsernameAvailability(@Path("userName") username: String): Boolean

}