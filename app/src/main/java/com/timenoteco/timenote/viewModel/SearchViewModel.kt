package com.timenoteco.timenote.viewModel

import android.content.SharedPreferences
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.timenoteco.timenote.adapter.UsersShareWithPagingAdapter
import com.timenoteco.timenote.model.Category
import com.timenoteco.timenote.model.TimenoteInfoDTO
import com.timenoteco.timenote.model.UserInfoDTO
import com.timenoteco.timenote.paging.SearchTagPagingSource
import com.timenoteco.timenote.paging.SearchUserByCategoryPagingSource
import com.timenoteco.timenote.paging.SearchUserPagingSource
import com.timenoteco.timenote.paging.UserPagingSource
import com.timenoteco.timenote.webService.repo.DayzeeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchViewModel : ViewModel() {

    private val searchService = DayzeeRepository().getSearchService()
    private val followService = DayzeeRepository().getFollowService()
    private val searchUserLiveData = MutableLiveData<Flow<PagingData<UserInfoDTO>>>()
    private val searchTagLiveData = MutableLiveData<Flow<PagingData<TimenoteInfoDTO>>>()

    fun searchUser(token: String, search: String, sharedPreferences: SharedPreferences) {
        searchUserLiveData.postValue(Pager(PagingConfig(pageSize = 1)){SearchUserPagingSource(token, search, searchService, sharedPreferences)}.flow.cachedIn(viewModelScope))
    }

    fun searchTag(token: String, search: String, sharedPreferences: SharedPreferences){
        searchTagLiveData.postValue(Pager(PagingConfig(pageSize = 1)){SearchTagPagingSource(token, search, searchService, sharedPreferences)}.flow.cachedIn(viewModelScope))
    }

    fun getUserSearchLiveData(): LiveData<Flow<PagingData<UserInfoDTO>>> {
        return searchUserLiveData
    }

    fun getTagSearchLiveData(): LiveData<Flow<PagingData<TimenoteInfoDTO>>> {
        return searchTagLiveData
    }

    fun getTop(token: String) = flow { emit(searchService.getTop("Bearer $token")) }.asLiveData(viewModelScope.coroutineContext)
    fun searchBasedOnCategory(token: String, category: Category) = Pager(PagingConfig(pageSize = 1)){SearchUserByCategoryPagingSource(token, category, searchService)}.flow.cachedIn(viewModelScope)

    fun searchChanged(token: String, search: String, sharedPreferences: SharedPreferences){
        searchUser(token, search, sharedPreferences)
        searchTag(token, search, sharedPreferences)
    }

}