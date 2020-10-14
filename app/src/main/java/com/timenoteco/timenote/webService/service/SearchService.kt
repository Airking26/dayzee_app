package com.timenoteco.timenote.webService.service

import com.timenoteco.timenote.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface SearchService {

    @GET("search/users/{name}/{offset}")
    suspend fun searchUser(@Header("Authorization") token: String, @Path("name") name: String, @Path("offset") offset : Int) : Response<List<UserInfoDTO>>

    @GET("search/timenotes/{tag}/{offset}")
    suspend fun searchTag(@Header("Authorization") token: String, @Path("tag") tag: String, @Path("offset") offset: Int) : Response<List<TimenoteInfoDTO>>

    @GET("search/category/users/{offset}")
    suspend fun searchBasedOnCategory(@Header("Authorization") token: String, @Body categories: Category, @Path("offset") offset: Int) : Response<List<UserInfoDTO>>

    @GET("search/top")
    suspend fun getTop(@Header("Authorization") token: String) : Response<List<PreferenceResultDTO>>

}