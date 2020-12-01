package com.timenoteco.timenote.paging

import android.content.SharedPreferences
import androidx.paging.PagingSource
import com.timenoteco.timenote.common.Utils
import com.timenoteco.timenote.model.TimenoteInfoDTO
import com.timenoteco.timenote.model.accessToken
import com.timenoteco.timenote.webService.service.AuthService
import com.timenoteco.timenote.webService.service.TimenoteService

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
}