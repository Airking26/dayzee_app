package com.dayzeeco.dayzee.paging

import android.content.SharedPreferences
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dayzeeco.dayzee.common.Utils
import com.dayzeeco.dayzee.model.TimenoteInfoDTO
import com.dayzeeco.dayzee.webService.service.AuthService
import com.dayzeeco.dayzee.webService.service.TimenoteService

class TimenoteRecentPagingSource(private val token: String, private val timenoteService: TimenoteService, private val sharedPreferences: SharedPreferences): PagingSource<Int, TimenoteInfoDTO>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TimenoteInfoDTO> {
        return try {
            val nextPageNumber = params.key ?: 0
            var response = timenoteService.getRecentTimenotes("Bearer $token", nextPageNumber)
            if(response.code() == 401) {
                response = timenoteService.getRecentTimenotes("Bearer ${Utils().refreshToken(sharedPreferences)}", nextPageNumber)
            }

            LoadResult.Page(
                data = response.body()!!,
                prevKey = null,
                nextKey = if(response.body()!!.isNotEmpty()) nextPageNumber + 1 else null
            )
        } catch (e: Exception){
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, TimenoteInfoDTO>): Int? {
        return 0
    }
}