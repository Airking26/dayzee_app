package com.dayzeeco.dayzee.paging

import android.content.SharedPreferences
import androidx.paging.PagingSource
import com.dayzeeco.dayzee.common.Utils
import com.dayzeeco.dayzee.model.NotificationInfoDTO
import com.dayzeeco.dayzee.webService.service.NotificationService

class NotificationPagingSource(val token: String, val id: String, val notificationService: NotificationService, val sharedPreferences: SharedPreferences): PagingSource<Int, NotificationInfoDTO>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NotificationInfoDTO> {
        return try {
            val nextPageNumber = params.key ?: 0
            var response = notificationService.getNotifications("Bearer $token", id, nextPageNumber)
            if(response.code() == 401){
                response = notificationService.getNotifications("Bearer ${Utils().refreshToken(sharedPreferences)}", id, nextPageNumber)
            }
            LoadResult.Page(
                data = response.body()!!,
                prevKey = null,
                nextKey = if(response.body()!!.isNotEmpty()) nextPageNumber + 1 else null
            )
        }catch (e:Exception){
            LoadResult.Error(e)
        }
    }
}