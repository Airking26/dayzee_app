package com.timenoteco.timenote.paging

import android.content.SharedPreferences
import androidx.paging.PagingSource
import com.timenoteco.timenote.model.CommentInfoDTO
import com.timenoteco.timenote.webService.service.CommentService
import mehdi.sakout.fancybuttons.Utils

class CommentPagingSource(val token: String, val id: String, val commentService: CommentService, val sharedPreferences: SharedPreferences): PagingSource<Int, CommentInfoDTO>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CommentInfoDTO> {
        return try {
            val nextPageNumber =  params.key ?: 0
            var response = commentService.getComments("Bearer $token", id, nextPageNumber)
            if(response.code() == 401) {
                response = commentService.getComments("Bearer ${com.timenoteco.timenote.common.Utils().refreshToken(sharedPreferences)}", id, nextPageNumber)
            }
            LoadResult.Page(
                data = response.body()!!,
                prevKey = null,
                nextKey = if(response.body()!!.isNotEmpty()) nextPageNumber + 1 else null
            )
        } catch (e: Exception){
            LoadResult.Error(e)
        }
    }
}