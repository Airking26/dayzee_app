package com.timenoteco.timenote.paging

import android.content.SharedPreferences
import androidx.paging.PagingSource
import com.timenoteco.timenote.common.Utils
import com.timenoteco.timenote.model.FilterLocationDTO
import com.timenoteco.timenote.model.TimenoteInfoDTO
import com.timenoteco.timenote.webService.service.TimenoteService

class TimenoteAroundPagingSource(val token: String, private val timenoteService: TimenoteService, val filterLocationDTO: FilterLocationDTO, val sharedPreferences: SharedPreferences): PagingSource<Int, TimenoteInfoDTO>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TimenoteInfoDTO> {
        return try {
            val nextPageNumber = params.key ?: 0
            var response = timenoteService.getAroundTimenotes("Bearer $token", nextPageNumber, filterLocationDTO)
            if(response.code() == 401) {
                response = timenoteService.getAroundTimenotes("Bearer ${Utils().refreshToken(sharedPreferences)}", nextPageNumber, filterLocationDTO)
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