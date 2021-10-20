package com.dayzeeco.dayzee.viewModel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.dayzeeco.dayzee.model.TimenoteHiddedCreationDTO
import com.dayzeeco.dayzee.paging.HiddedEventsPagingSource
import com.dayzeeco.dayzee.paging.HiddedUsersPagingSource
import com.dayzeeco.dayzee.webService.repo.DayzeeRepository
import com.dayzeeco.dayzee.webService.service.TimenoteHiddedService
import kotlinx.coroutines.flow.flow

class TimenoteHiddedViewModel: ViewModel() {

    private val timenoteHiddedService: TimenoteHiddedService = DayzeeRepository().getTimenoteHiddedService()

    fun hideEventOrUSer(token: String, timenoteHiddedCreationDTO: TimenoteHiddedCreationDTO) = flow { emit(timenoteHiddedService.hideEventOrUser("Bearer $token", timenoteHiddedCreationDTO)) }.asLiveData(viewModelScope.coroutineContext)
    fun getUsersHidden(token: String, sharedPreferences: SharedPreferences) = Pager(PagingConfig(pageSize = 1)){HiddedUsersPagingSource(token, timenoteHiddedService, sharedPreferences)}.flow.cachedIn(viewModelScope)
    fun getEventsHidden(token: String, sharedPreferences: SharedPreferences) = Pager(PagingConfig(pageSize = 1)){HiddedEventsPagingSource(token, timenoteHiddedService, sharedPreferences)}.flow.cachedIn(viewModelScope)
    fun removeUserFromHiddens(token: String, id: String) = flow { emit(timenoteHiddedService.removeUserFromHiddens("Bearer $token", id)) }.asLiveData(viewModelScope.coroutineContext)
    fun removeEventFromHiddens(token: String, id: String) = flow { emit(timenoteHiddedService.removeEventFromHiddens("Bearer $token", id)) }.asLiveData(viewModelScope.coroutineContext)
}