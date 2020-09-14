package com.timenoteco.timenote.paging

import androidx.paging.PagingSource
import com.timenoteco.timenote.model.TimenoteInfoDTO
import com.timenoteco.timenote.webService.service.ProfileService

class ProfileEventPagingSource(val token : String, val id: String, val profileService: ProfileService, val future: Boolean): PagingSource<Int, TimenoteInfoDTO>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TimenoteInfoDTO> {
        return try {
            val nextPageNumber = params.key ?: 0
            val response = if(future) profileService.getFutureTimenotes("Bearer $token", id, nextPageNumber)
            else profileService.getPastTimenotes("Bearer $token", nextPageNumber)
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

