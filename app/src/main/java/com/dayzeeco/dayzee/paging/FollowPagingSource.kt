package com.dayzeeco.dayzee.paging

import android.content.SharedPreferences
import androidx.paging.PagingSource
import com.dayzeeco.dayzee.common.Utils
import com.dayzeeco.dayzee.model.UserInfoDTO
import com.dayzeeco.dayzee.webService.service.FollowService
import java.lang.Exception

class FollowPagingSource(val token: String, val followService: FollowService, val followersWaitingForApproval: Boolean, val sharedPreferences: SharedPreferences): PagingSource<Int, UserInfoDTO>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserInfoDTO> {
        return try {
            val nextPageNumber = params.key ?: 0
            var response = if(followersWaitingForApproval) followService.getUsersWaitingForApproval("Bearer $token", nextPageNumber)
            else followService.getUsersRequestedToFollow("Bearer $token", nextPageNumber)
            if(response.code() == 401){
                response = if(followersWaitingForApproval) followService.getUsersWaitingForApproval("Bearer ${Utils().refreshToken(sharedPreferences)}", nextPageNumber)
                else followService.getUsersRequestedToFollow("Bearer ${Utils().refreshToken(sharedPreferences)}", nextPageNumber)
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