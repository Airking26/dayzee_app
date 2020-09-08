package com.timenoteco.timenote.paging

import androidx.paging.PagingSource
import com.timenoteco.timenote.model.UserInfoDTO
import com.timenoteco.timenote.webService.service.FollowService
import com.timenoteco.timenote.webService.service.TimenoteService

class UserPagingSource(val token: String, private val followService: FollowService,
                       val followers: Boolean, private val timenoteService: TimenoteService,
                       private val useTimenoteService: Boolean, val id: String?): PagingSource<Int, UserInfoDTO>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserInfoDTO> {
        return try {
            val nextPageNumber = params.key ?: 1
            val response =
                if(useTimenoteService){
                    timenoteService.getUsersParticipatingTimenote("Bearer $token",id!!, nextPageNumber)
                } else {
                    if(followers) followService.getFollowedUsers("Bearer $token",nextPageNumber)
                    else followService.getFollowingUsers("Bearer $token",nextPageNumber)
                }

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