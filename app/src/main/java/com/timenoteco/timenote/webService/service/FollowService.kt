package com.timenoteco.timenote.webService.service

import com.timenoteco.timenote.model.UserResponse
import retrofit2.Response
import retrofit2.http.*

interface FollowService {

    @GET("follow/me/pending/{offset}")
    suspend fun getUsersWaitingForApproval(@Header("Authorization") token: String, @Path("offset") offset: Int) : Response<List<UserResponse>>

    @GET("follow/me/requested/{offset}")
    suspend fun getUsersRequestedToFollow(@Header("Authorization") token: String, @Path("offset") offset: Int) : Response<List<UserResponse>>

    @GET("follow/me/following/{offset}")
    suspend fun getFollowingUsers(@Header("Authorization") token: String, @Path("offset") offset: Int) : Response<List<UserResponse>>

    @GET("follow/me/followers/{offset}")
    suspend fun getFollowedUsers(@Header("Authorization") token: String, @Path("offset") offset: Int) : Response<List<UserResponse>>

    @PUT("follow/me/{id}")
    suspend fun followPublicUser(@Header("Authorization") token: String, @Path("id") id: Int) : Response<UserResponse>

    @PUT("follow/me/accept/{id}")
    suspend fun acceptFollowingRequest(@Header("Authorization") token: String, @Path("id") id: Int) : Response<UserResponse>

    @PUT("follow/me/decline/{id}")
    suspend fun declineFollowingRequest(@Header("Authorization") token: String, @Path("id") id: Int) : Response<UserResponse>

    @DELETE("follow/me/{id}")
    suspend fun unfollowUser(@Header("Authorization") token: String, @Path("id") id : Int) : Response<Any>

    @DELETE("follow/me/remove/{id}")
    suspend fun removeUserFromFollowers(@Header("Authorization") token: String, @Path("id") id: Int) : Response<Any>

    @POST("follow/me/ask/{id}")
    suspend fun followPrivateUser(@Header("Authorization") token: String, @Path("id") id: Int) : Response<Any>
}