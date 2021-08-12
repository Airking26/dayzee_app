package com.dayzeeco.dayzee.viewModel

import android.content.SharedPreferences
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dayzeeco.dayzee.model.Category
import com.dayzeeco.dayzee.model.TimenoteInfoDTO
import com.dayzeeco.dayzee.model.UserInfoDTO
import com.dayzeeco.dayzee.paging.SearchTagPagingSource
import com.dayzeeco.dayzee.paging.SearchUserByCategoryPagingSource
import com.dayzeeco.dayzee.paging.SearchUserPagingSource
import com.dayzeeco.dayzee.webService.repo.DayzeeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchViewModel : ViewModel() {

    private val searchService = DayzeeRepository().getSearchService()
    private val searchUserLiveData = MutableLiveData<Flow<PagingData<UserInfoDTO>>>()
    private val searchTagLiveData = MutableLiveData<Flow<PagingData<TimenoteInfoDTO>>>()
    private val searchIsEmptyLiveData = MutableLiveData<Boolean>()

    private fun searchUser(token: String, search: String, sharedPreferences: SharedPreferences) {
        searchUserLiveData.postValue(Pager(PagingConfig(pageSize = 1)){SearchUserPagingSource(token, search, searchService, sharedPreferences)}.flow.cachedIn(viewModelScope))
    }

    fun getUsers(token: String, search: String, sharedPreferences: SharedPreferences): Flow<PagingData<UserInfoDTO>> = Pager(PagingConfig(pageSize = 1)){SearchUserPagingSource(token, search, searchService, sharedPreferences)}.flow.cachedIn(viewModelScope)


    private fun searchTag(token: String, search: String, sharedPreferences: SharedPreferences){
        searchTagLiveData.postValue(Pager(PagingConfig(pageSize = 1)){SearchTagPagingSource(token, search, searchService, sharedPreferences)}.flow.cachedIn(viewModelScope))
    }

    fun getHashtags(token: String, search: String, sharedPreferences: SharedPreferences) = Pager(
        PagingConfig(pageSize = 1)){SearchTagPagingSource(token, search, searchService, sharedPreferences)}.flow.cachedIn(viewModelScope)

    fun getSearchIsEmptyLiveData(): LiveData<Boolean> {
        return searchIsEmptyLiveData
    }

    fun setSearchIsEmpty(isEmpty: Boolean){
        searchIsEmptyLiveData.postValue(isEmpty)
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