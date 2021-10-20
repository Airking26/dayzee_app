package com.dayzeeco.dayzee.paging

import android.content.SharedPreferences
import androidx.paging.PagingSource
import com.dayzeeco.dayzee.common.Utils
import com.dayzeeco.dayzee.model.TimenoteInfoDTO
import com.dayzeeco.dayzee.webService.service.TimenoteHiddedService

class HiddedEventsPagingSource(val token: String, val hiddedService: TimenoteHiddedService, val sharedPreferences: SharedPreferences): PagingSource<Int, TimenoteInfoDTO>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TimenoteInfoDTO> {
        return try {
            val nextPageNumber = params.key ?: 0
            var response = hiddedService.getHiddenEvents("Bearer $token", nextPageNumber)
            if(response.code() == 401){
                response = hiddedService.getHiddenEvents("Bearer ${Utils().refreshToken(sharedPreferences)}", nextPageNumber)
            }
            LoadResult.Page(
                data = response.body()!!,
                prevKey = null,
                nextKey = if(response.body()!!.isNotEmpty()) nextPageNumber + 1 else null
            )
        }catch (e:Exception){
            LoadResult.Error(e)
        }
    }
}