package com.dayzeeco.dayzee.paging

import android.content.SharedPreferences
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dayzeeco.dayzee.common.Utils
import com.dayzeeco.dayzee.model.TimenoteInfoDTO
import com.dayzeeco.dayzee.model.UserInfoDTO
import com.dayzeeco.dayzee.webService.service.TimenoteService
import java.lang.Error

class UsersParticipatingPagingSource(val token: String, val id: String, val timenoteService: TimenoteService, val sharedPreferences: SharedPreferences): PagingSource<Int, UserInfoDTO>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserInfoDTO> {
        return try {
            val nextPageNumber = params.key ?: 0
            var response = timenoteService.getUsersParticipatingTimenote("Bearer $token", id, nextPageNumber)
            if(response.code() == 401){
                response = timenoteService.getUsersParticipatingTimenote("Bearer ${Utils().refreshToken(sharedPreferences)}", id, nextPageNumber)
            }
            LoadResult.Page(
                data = response.body()!!,
                prevKey = null,
                nextKey = if(response.body()!!.isNotEmpty()) nextPageNumber + 1 else null
            )
        } catch (e: Error){
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, UserInfoDTO>): Int? {
        return 0
    }

}