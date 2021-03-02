package com.dayzeeco.dayzee.paging

import android.content.SharedPreferences
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dayzeeco.dayzee.common.Utils
import com.dayzeeco.dayzee.model.TimenoteInfoDTO
import com.dayzeeco.dayzee.model.UserInfoDTO
import com.dayzeeco.dayzee.webService.service.SearchService

class SearchTagPagingSource(val token: String, val search: String, val searchService: SearchService, val sharedPreferences: SharedPreferences) : PagingSource<Int, TimenoteInfoDTO>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TimenoteInfoDTO> {
        return try {
            val nextPageNumber = params.key ?: 0
            var response = searchService.searchTag("Bearer $token", search, nextPageNumber)
            if(response.code() == 401){
                response = searchService.searchTag("Bearer ${Utils().refreshToken(sharedPreferences)}", search, nextPageNumber)
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