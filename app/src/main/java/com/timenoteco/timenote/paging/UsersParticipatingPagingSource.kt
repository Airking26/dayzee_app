package com.timenoteco.timenote.paging

import androidx.paging.PagingSource
import com.timenoteco.timenote.model.UserInfoDTO
import com.timenoteco.timenote.webService.service.TimenoteService
import java.lang.Error

class UsersParticipatingPagingSource(val token: String, val id: String, val timenoteService: TimenoteService): PagingSource<Int, UserInfoDTO>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserInfoDTO> {
        return try {
            val nextPageNumber = params.key ?: 0
            val response = timenoteService.getUsersParticipatingTimenote("Bearer $token", id, nextPageNumber)
            LoadResult.Page(
                data = response.body()!!,
                prevKey = null,
                nextKey = if(response.body()!!.isNotEmpty()) nextPageNumber + 1 else null
            )
        } catch (e: Error){
            LoadResult.Error(e)
        }
    }
}