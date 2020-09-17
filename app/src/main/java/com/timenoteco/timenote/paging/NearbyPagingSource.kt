package com.timenoteco.timenote.paging

import androidx.paging.PagingSource
import com.timenoteco.timenote.model.NearbyRequestBody
import com.timenoteco.timenote.model.TimenoteInfoDTO
import com.timenoteco.timenote.webService.service.NearbyService
import java.lang.Error

class NearbyPagingSource(val nearbyRequestBody: NearbyRequestBody, val nearbyService: NearbyService) : PagingSource<Int, TimenoteInfoDTO>(){

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TimenoteInfoDTO> {
        return try {
            val nextPageNumber = params.key ?: 0
            val response = nearbyService.getNearbyResults(nearbyRequestBody)
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