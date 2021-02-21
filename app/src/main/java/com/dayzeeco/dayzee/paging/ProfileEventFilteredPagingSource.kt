package com.dayzeeco.dayzee.paging

import android.content.SharedPreferences
import androidx.paging.PagingSource
import com.dayzeeco.dayzee.common.Utils
import com.dayzeeco.dayzee.model.TimenoteFilteredDTO
import com.dayzeeco.dayzee.model.TimenoteInfoDTO
import com.dayzeeco.dayzee.webService.service.ProfileService

class ProfileEventFilteredPagingSource(val token : String, val timenoteFilteredDTO: TimenoteFilteredDTO, val profileService: ProfileService, val sharedPreferences: SharedPreferences): PagingSource<Int, TimenoteInfoDTO>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TimenoteInfoDTO> {
        return try {
            val nextPageNumber = params.key ?: 0
            var response = profileService.getTimenotesFiltered("Bearer $token", nextPageNumber, timenoteFilteredDTO)
            if(response.code() == 401){
                response = profileService.getTimenotesFiltered("Bearer ${Utils().refreshToken(sharedPreferences)}", nextPageNumber, timenoteFilteredDTO)
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

