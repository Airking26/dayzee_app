package com.timenoteco.timenote.paging

import androidx.paging.PagingSource
import com.timenoteco.timenote.model.NearbyRequestBody
import com.timenoteco.timenote.model.TimenoteInfoDTO
import com.timenoteco.timenote.webService.service.NearbyService
import java.lang.Error

class NearbyPagingSource(val token : String, val nearbyRequestBody: NearbyRequestBody, val nearbyService: NearbyService) : PagingSource<Int, TimenoteInfoDTO>(){

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TimenoteInfoDTO> {
        return try {
            val nextPageNumber = params.key ?: 0
            val response = nearbyService.getNearbyResults(token, nextPageNumber, nearbyRequestBody)
            LoadResult.Page(
                data = response.body()!!,
                prevKey = null,
                nextKey = if(response.body()!!.isNullOrEmpty()) null else nextPageNumber + 1
            )
        } catch (e: Error){
            LoadResult.Error(e)
        }
    }

}