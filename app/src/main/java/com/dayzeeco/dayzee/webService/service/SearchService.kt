package com.dayzeeco.dayzee.webService.service

import com.dayzeeco.dayzee.model.*
import retrofit2.Response
import retrofit2.http.*

interface SearchService {

    @GET("search/users/{name}/{offset}")
    suspend fun searchUser(@Header("Authorization") token: String, @Path("name") name: String, @Path("offset") offset : Int) : Response<List<UserInfoDTO>>

    @GET("search/timenotes/{tag}/{offset}")
    suspend fun searchTag(@Header("Authorization") token: String, @Path("tag") tag: String, @Path("offset") offset: Int) : Response<List<TimenoteInfoDTO>>

    @POST("search/category/users/{offset}")
    suspend fun searchBasedOnCategory(@Header("Authorization") token: String, @Body categories: Category, @Path("offset") offset: Int) : Response<List<UserInfoDTO>>

    @POST("search/category/top/users")
    suspend fun getTopUsers(@Header("Authorization") token: String, @Body categories: Category) : Response<List<UserInfoDTO>>

    @GET("search/tops")
    suspend fun getTop(@Header("Authorization") token: String) : Response<List<PreferenceResultDTO>>

}