package com.timenoteco.timenote.paging

import androidx.paging.PagingSource
import com.timenoteco.timenote.model.TimenoteModel
import com.timenoteco.timenote.webService.service.TimenoteService

class TimenoteRemotePagingSource(val timenoteService: TimenoteService):
    PagingSource<Int, TimenoteModel>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TimenoteModel> {
        return try {
            val nextPageNumber = params.key ?: 1
            val response = timenoteService.getAllTimenotes(nextPageNumber)
            LoadResult.Page(
                data = response.body()!!,
                prevKey = null,
                nextKey = nextPageNumber + 1
            )
        } catch (e: Exception){
            LoadResult.Error(e)
        }
    }
}