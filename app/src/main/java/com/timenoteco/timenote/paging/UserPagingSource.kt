package com.timenoteco.timenote.paging

import androidx.paging.PagingSource
import com.timenoteco.timenote.model.UserInfoDTO
import com.timenoteco.timenote.webService.service.FollowService
import com.timenoteco.timenote.webService.service.TimenoteService

class UserPagingSource(val token: String, private val followService: FollowService, val followers: Int): PagingSource<Int, UserInfoDTO>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserInfoDTO> {
        return try {
            val nextPageNumber = params.key ?: 0
            val response =
                    if(followers == 1) followService.getFollowedUsers("Bearer $token",nextPageNumber)
                    else followService.getFollowingUsers("Bearer $token",nextPageNumber)

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