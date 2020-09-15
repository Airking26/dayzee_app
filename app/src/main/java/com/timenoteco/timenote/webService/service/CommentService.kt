package com.timenoteco.timenote.webService.service

import com.timenoteco.timenote.model.CommentCreationDTO
import com.timenoteco.timenote.model.CommentInfoDTO
import retrofit2.Response
import retrofit2.http.*

interface CommentService {

    @GET("comment/{id}/{offset}")
    suspend fun getComments(@Header("Authorization") token: String, @Path("id") id: String, @Path("offset") offset: Int) : Response<List<CommentInfoDTO>>

    @POST("comment/create")
    suspend fun postComment(@Header("Authorization") token: String, @Body commentCreationDTO: CommentCreationDTO) : Response<CommentInfoDTO>
}