package com.timenoteco.timenote.paging

import androidx.paging.PagingSource
import com.timenoteco.timenote.model.CommentInfoDTO
import com.timenoteco.timenote.webService.service.CommentService

class CommentPagingSource(val token: String, val id: String, val commentService: CommentService): PagingSource<Int, CommentInfoDTO>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CommentInfoDTO> {
        return try {
            val nextPageNumber =  params.key ?: 0
            val response = commentService.getComments("Bearer $token", id, nextPageNumber)
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