package com.timenoteco.timenote.paging

import android.content.SharedPreferences
import androidx.paging.PagingSource
import com.timenoteco.timenote.common.Utils
import com.timenoteco.timenote.model.NearbyRequestBody
import com.timenoteco.timenote.model.TimenoteInfoDTO
import com.timenoteco.timenote.webService.service.NearbyService
import java.lang.Error

class NearbyPagingSource(val token : String, private val nearbyRequestBody: NearbyRequestBody, private val nearbyService: NearbyService, val sharedPreferences: SharedPreferences) : PagingSource<Int, TimenoteInfoDTO>(){

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TimenoteInfoDTO> {
        return try {
            val nextPageNumber = params.key ?: 0
            var response = nearbyService.getNearbyResults("Bearer $token", nextPageNumber, nearbyRequestBody)
            if(response.code() == 401){
                response = nearbyService.getNearbyResults("Bearer ${Utils().refreshToken(sharedPreferences)!!}", nextPageNumber, nearbyRequestBody)
            }
            LoadResult.Page(
                data = response.body()!!,
                prevKey = null,
                nextKey = if(response.body()!!.isNullOrEmpty()) null else nextPageNumber + 1
            )
        } catch (e: Error){
            LoadResult.Error(e)
        }
    }

}