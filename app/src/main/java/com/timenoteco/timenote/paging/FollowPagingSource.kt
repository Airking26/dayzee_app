package com.timenoteco.timenote.paging

import androidx.paging.PagingSource
import com.timenoteco.timenote.model.UserResponse
import com.timenoteco.timenote.webService.service.FollowService
import java.lang.Exception

class FollowPagingSource(val token: String, val followService: FollowService, val followersWaitingForApproval: Boolean): PagingSource<Int, UserResponse>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserResponse> {
        return try {
            val nextPageNumber = params.key ?: 1
            val response = if(followersWaitingForApproval) followService.getUsersWaitingForApproval("Bearer $token", nextPageNumber)
            else followService.getUsersRequestedToFollow("Bearer $token", nextPageNumber)
            LoadResult.Page(
                data = response.body()!!,
                prevKey = null,
                nextKey = nextPageNumber + 1
            )
        } catch (e: Exception){
            LoadResult.Error(e)
        }
    }
}