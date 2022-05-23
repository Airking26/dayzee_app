package com.dayzeeco.dayzee.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dayzeeco.dayzee.model.Category
import com.dayzeeco.dayzee.model.TimenoteInfoDTO
import com.dayzeeco.dayzee.model.UserInfoDTO
import com.dayzeeco.dayzee.webService.service.SearchService

class SearchUserByCategoryPagingSource(val token: String, val category: Category, val searchService: SearchService) : PagingSource<Int, UserInfoDTO>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserInfoDTO> {
        return try {
            val nextPageNumber = params.key ?: 0
            val response = searchService.searchBasedOnCategory("Bearer $token", category, nextPageNumber)
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