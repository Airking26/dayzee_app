package com.dayzeeco.dayzee.webService.service

import com.dayzeeco.dayzee.model.CommentCreationDTO
import com.dayzeeco.dayzee.model.CommentInfoDTO
import retrofit2.Response
import retrofit2.http.*

interface CommentService {

    @GET("comment/{id}/{offset}")
    suspend fun getComments(@Header("Authorization") token: String, @Path("id") id: String, @Path("offset") offset: Int) : Response<List<CommentInfoDTO>>

    @POST("comment/create")
    suspend fun postComment(@Header("Authorization") token: String, @Body commentCreationDTO: CommentCreationDTO) : Response<CommentInfoDTO>

    @DELETE("comment/delete/{id}")
    suspend fun deleteComment(@Header("Authorization") token: String, @Path("id") id: String): Response<CommentInfoDTO>
}