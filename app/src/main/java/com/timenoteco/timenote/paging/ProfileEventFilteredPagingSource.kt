package com.timenoteco.timenote.paging

import androidx.paging.PagingSource
import com.timenoteco.timenote.model.TimenoteFilteredDTO
import com.timenoteco.timenote.model.TimenoteInfoDTO
import com.timenoteco.timenote.webService.service.ProfileService

class ProfileEventFilteredPagingSource(val token : String, val timenoteFilteredDTO: TimenoteFilteredDTO, val profileService: ProfileService): PagingSource<Int, Any>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Any> {
        return try {
            val nextPageNumber = params.key ?: 0
            val response = profileService.getTimenotesFiltered("Bearer $token", nextPageNumber, timenoteFilteredDTO)
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

