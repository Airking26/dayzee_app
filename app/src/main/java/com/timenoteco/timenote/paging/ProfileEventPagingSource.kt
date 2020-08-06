package com.timenoteco.timenote.paging

import androidx.paging.PagingSource
import com.timenoteco.timenote.model.TimenoteModel
import com.timenoteco.timenote.webService.service.ProfileService

class ProfileEventPagingSource(val profileService: ProfileService, val future: Boolean): PagingSource<Int, TimenoteModel>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TimenoteModel> {
        return try {
            val nextPageNumber = params.key ?: 1
            val response = if(future) profileService.getFutureTimenotes("Bearer " + "",nextPageNumber) else profileService.getPastTimenotes("Bearer " + "",nextPageNumber)
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

