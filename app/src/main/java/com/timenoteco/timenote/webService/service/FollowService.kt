package com.timenoteco.timenote.webService.service

import com.timenoteco.timenote.model.UserResponse
import retrofit2.Response
import retrofit2.http.*

interface FollowService {

    @GET("follow/me/pending/{offset}")
    suspend fun getUsersWaitingForApproval(@Path("offset") offset: Int) : Response<List<UserResponse>>

    @GET("follow/me/requested/{offset}")
    suspend fun getUsersRequestedToFollow(@Path("offset") offset: Int) : Response<List<UserResponse>>

    @GET("follow/me/following/{offset}")
    suspend fun getFollowingUsers(@Path("offset") offset: Int) : Response<List<UserResponse>>

    @GET("follow/me/followers/{offset}")
    suspend fun getFollowedUsers(@Path("offset") offset: Int) : Response<List<UserResponse>>

    @PUT("follow/me/{id}")
    suspend fun followPublicUser(@Path("id") id: Int) : Response<UserResponse>

    @PUT("follow/me/accept/{id}")
    suspend fun acceptFollowingRequest(@Path("id") id: Int) : Response<UserResponse>

    @PUT("follow/me/decline/{id}")
    suspend fun declineFollowingRequest(@Path("id") id: Int) : Response<UserResponse>

    @DELETE("follow/me/{id}")
    suspend fun unfollowUser(@Path("id") id : Int) : Response<Any>

    @DELETE("follow/me/remove/{id}")
    suspend fun removeUserFromFollowers(@Path("id") id: Int) : Response<Any>

    @POST("follow/me/ask/{id}")
    suspend fun followPrivateUser(@Path("id") id: Int) : Response<Any>
}