package com.dayzeeco.dayzee.paging

import android.content.SharedPreferences
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dayzeeco.dayzee.common.Utils
import com.dayzeeco.dayzee.model.NearbyRequestBody
import com.dayzeeco.dayzee.model.TimenoteInfoDTO
import com.dayzeeco.dayzee.webService.service.NearbyService
import java.lang.Error

class NearbyPagingSource(private val nearbyRequestBody: NearbyRequestBody,
                         private val nearbyService: NearbyService,
                         val sharedPreferences: SharedPreferences) : PagingSource<Int, TimenoteInfoDTO>(){

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TimenoteInfoDTO> {
        return try {
            val nextPageNumber = params.key ?: 0
            val response = nearbyService.getNearbyResults(nextPageNumber, nearbyRequestBody)
            LoadResult.Page(
                data = response.body()!!,
                prevKey = null,
                nextKey = if(response.body()!!.isNullOrEmpty()) null else nextPageNumber + 1
            )
        } catch (e: Error){
            val i = e
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, TimenoteInfoDTO>): Int? {
        return 0
    }

}