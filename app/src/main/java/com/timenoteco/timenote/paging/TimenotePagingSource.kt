package com.timenoteco.timenote.paging

import androidx.paging.PagingSource
import com.timenoteco.timenote.model.TimenoteInfoDTO
import com.timenoteco.timenote.webService.service.TimenoteService

class TimenotePagingSource(val token: String?, val timenoteService: TimenoteService, val upcoming : Boolean): PagingSource<Int, TimenoteInfoDTO>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TimenoteInfoDTO> {
        return try {
            val nextPageNumber = params.key ?: 0
            val response = if(upcoming) timenoteService.getUpcomingTimenotes("Bearer $token", nextPageNumber) else timenoteService.getPastTimenotes("Bearer $token", nextPageNumber)
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