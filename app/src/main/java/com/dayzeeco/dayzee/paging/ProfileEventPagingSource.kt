package com.dayzeeco.dayzee.paging

import android.content.SharedPreferences
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dayzeeco.dayzee.common.Utils
import com.dayzeeco.dayzee.model.TimenoteInfoDTO
import com.dayzeeco.dayzee.webService.service.ProfileService

class ProfileEventPagingSource(val token : String, val id: String, val profileService: ProfileService, val future: Boolean, val sharedPreferences: SharedPreferences): PagingSource<Int, TimenoteInfoDTO>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TimenoteInfoDTO> {
        return try {
            val nextPageNumber = params.key ?: 0
            var response = if(future) profileService.getFutureTimenotes("Bearer $token", id, nextPageNumber)
            else profileService.getPastTimenotes("Bearer $token", id, nextPageNumber)
            if(response.code() == 401){
                response = if(future) profileService.getFutureTimenotes("Bearer ${Utils().refreshToken(sharedPreferences)}", id, nextPageNumber)
                else profileService.getPastTimenotes("Bearer ${Utils().refreshToken(sharedPreferences)}", id, nextPageNumber)
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

