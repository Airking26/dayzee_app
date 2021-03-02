package com.dayzeeco.dayzee.paging

import android.content.SharedPreferences
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dayzeeco.dayzee.common.Utils
import com.dayzeeco.dayzee.model.FilterLocationDTO
import com.dayzeeco.dayzee.model.TimenoteInfoDTO
import com.dayzeeco.dayzee.webService.service.TimenoteService

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