package com.timenoteco.timenote.paging

import android.content.SharedPreferences
import androidx.paging.PagingSource
import com.timenoteco.timenote.common.Utils
import com.timenoteco.timenote.model.UserInfoDTO
import com.timenoteco.timenote.webService.service.SearchService

class SearchUserPagingSource(val token: String, val search: String, private val searchService: SearchService, val sharedPreferences: SharedPreferences) : PagingSource<Int, UserInfoDTO>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserInfoDTO> {
        return try {
            val nextPageNumber = params.key ?: 0
            var response = searchService.searchUser("Bearer $token", search, nextPageNumber)
            if(response.code() == 401){
                response = searchService.searchUser("Bearer ${Utils().refreshToken(sharedPreferences)}", search, nextPageNumber)
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