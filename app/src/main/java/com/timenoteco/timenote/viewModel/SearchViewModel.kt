package com.timenoteco.timenote.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.timenoteco.timenote.model.TimenoteInfoDTO
import com.timenoteco.timenote.model.UserInfoDTO
import com.timenoteco.timenote.paging.SearchTagPagingSource
import com.timenoteco.timenote.paging.SearchUserPagingSource
import com.timenoteco.timenote.webService.repo.DayzeeRepository
import kotlinx.coroutines.flow.Flow

class SearchViewModel : ViewModel() {

    private val searchService = DayzeeRepository().getSearchService()
    private val searchUserLiveData = MutableLiveData<Flow<PagingData<UserInfoDTO>>>()
    private val searchTagLiveData = MutableLiveData<Flow<PagingData<TimenoteInfoDTO>>>()

    fun searchUser(token: String, search: String) {
        searchUserLiveData.postValue(Pager(PagingConfig(pageSize = 1)){SearchUserPagingSource(token, search, searchService)}.flow.cachedIn(viewModelScope))
    }

    fun searchTag(token: String, search: String){
        searchTagLiveData.postValue(Pager(PagingConfig(pageSize = 1)){SearchTagPagingSource(token, search, searchService)}.flow.cachedIn(viewModelScope))
    }

    fun getUserSearchLiveData(): LiveData<Flow<PagingData<UserInfoDTO>>> {
        return searchUserLiveData
    }

    fun getTagSearchLiveData(): LiveData<Flow<PagingData<TimenoteInfoDTO>>> {
        return searchTagLiveData
    }

    fun searchChanged(token: String, search: String){
        searchUser(token, search)
        searchTag(token, search)
    }

}