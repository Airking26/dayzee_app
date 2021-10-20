package com.dayzeeco.dayzee.paging

import android.content.SharedPreferences
import androidx.paging.PagingSource
import com.dayzeeco.dayzee.common.Utils
import com.dayzeeco.dayzee.model.TimenoteInfoDTO
import com.dayzeeco.dayzee.model.UserInfoDTO
import com.dayzeeco.dayzee.webService.service.TimenoteHiddedService

class HiddedUsersPagingSource(val token: String, val hiddedService: TimenoteHiddedService, val sharedPreferences: SharedPreferences): PagingSource<Int, UserInfoDTO>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserInfoDTO> {
        return try {
            val nextPageNumber = params.key ?: 0
            var response = hiddedService.getHiddenUsers("Bearer $token", nextPageNumber)
            if(response.code() == 401){
                response = hiddedService.getHiddenUsers("Bearer ${Utils().refreshToken(sharedPreferences)}", nextPageNumber)
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