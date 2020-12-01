package com.timenoteco.timenote.paging

import android.content.SharedPreferences
import androidx.paging.PagingSource
import com.timenoteco.timenote.common.Utils
import com.timenoteco.timenote.model.UserInfoDTO
import com.timenoteco.timenote.webService.service.FollowService
import com.timenoteco.timenote.webService.service.TimenoteService

class UserPagingSource(val token: String, private val id: String, private val followService: FollowService, val followers: Int, val sharedPreferences: SharedPreferences): PagingSource<Int, UserInfoDTO>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserInfoDTO> {
        return try {
            val nextPageNumber = params.key ?: 0
            var response =
                    if(followers == 1) followService.getFollowedUsers("Bearer $token",id, nextPageNumber)
                    else followService.getFollowingUsers("Bearer $token",id, nextPageNumber)
            if(response.code() == 401){
                if(followers == 1) followService.getFollowedUsers("Bearer ${Utils().refreshToken(sharedPreferences)}", id, nextPageNumber)
                else followService.getFollowingUsers("Bearer ${Utils().refreshToken(sharedPreferences)}", id, nextPageNumber)
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