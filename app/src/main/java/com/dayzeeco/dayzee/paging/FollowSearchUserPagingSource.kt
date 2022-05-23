package com.dayzeeco.dayzee.paging

import android.content.SharedPreferences
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dayzeeco.dayzee.common.Utils
import com.dayzeeco.dayzee.model.CommentInfoDTO
import com.dayzeeco.dayzee.model.UserInfoDTO
import com.dayzeeco.dayzee.webService.service.FollowService
import com.dayzeeco.dayzee.webService.service.SearchService

class FollowSearchUserPagingSource(val token: String, val search: String, val followService: FollowService, val sharedPreferences: SharedPreferences) : PagingSource<Int, UserInfoDTO>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserInfoDTO> {
        return try {
            val nextPageNumber = params.key ?: 0
            var response = followService.searchInFollowing("Bearer $token", search, nextPageNumber)
            if(response.code() == 401){
                response = followService.searchInFollowing("Bearer ${Utils().refreshToken(sharedPreferences)}", search, nextPageNumber)
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

    override fun getRefreshKey(state: PagingState<Int, UserInfoDTO>): Int? {
        return 0
    }

}