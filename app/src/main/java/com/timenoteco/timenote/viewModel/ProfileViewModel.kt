package com.timenoteco.timenote.viewModel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.timenoteco.timenote.model.*
import com.timenoteco.timenote.paging.ProfileEventFilteredPagingSource
import com.timenoteco.timenote.paging.ProfileEventPagingSource
import com.timenoteco.timenote.paging.UserPagingSource
import com.timenoteco.timenote.webService.repo.DayzeeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProfileViewModel: ViewModel() {

    private val profileService = DayzeeRepository().getProfileService()

    fun getEventProfile(token: String, id: String, future: Boolean, sharedPreferences: SharedPreferences) = Pager(PagingConfig(pageSize = 1)){ProfileEventPagingSource(token, id, profileService, future, sharedPreferences)}.flow.cachedIn(viewModelScope)
    fun createGroup(token: String, createGroupDTO: CreateGroupDTO) = flow { emit(profileService.createGroup("Bearer $token", createGroupDTO))}.asLiveData(viewModelScope.coroutineContext)
    fun modifyGroup(token: String, id: String, createGroupDTO: CreateGroupDTO) = flow {emit(profileService.modifyGroup("Bearer $token", id, createGroupDTO))}.asLiveData(viewModelScope.coroutineContext)
    fun deleteGroup(token: String, id: String) = flow { emit(profileService.deleteGroup("Bearer $token", id)) }.asLiveData(viewModelScope.coroutineContext)
    fun getAllGroups(token: String) = flow { emit(profileService.getAllGroups("Bearer $token")) }.asLiveData(viewModelScope.coroutineContext)
    fun getTimenotesByDate(token: String, timenoteDateFilteredDTO: TimenoteDateFilteredDTO, id: String) = flow { emit(profileService.getTimenoteByDate("Bearer $token", id, timenoteDateFilteredDTO))}.asLiveData(viewModelScope.coroutineContext)
    fun getTimenotesFiltered(token: String, timenoteFilteredDTO: TimenoteFilteredDTO, sharedPreferences: SharedPreferences) = Pager(PagingConfig(pageSize = 1)){ProfileEventFilteredPagingSource(token, timenoteFilteredDTO, profileService, sharedPreferences)}.flow.cachedIn(viewModelScope)

}