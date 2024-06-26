package com.dayzeeco.dayzee.viewModel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dayzeeco.dayzee.model.*
import com.dayzeeco.dayzee.paging.TimenoteAroundPagingSource
import com.dayzeeco.dayzee.paging.TimenoteRecentPagingSource
import com.dayzeeco.dayzee.paging.TimenotePagingSource
import com.dayzeeco.dayzee.paging.UsersParticipatingPagingSource
import com.dayzeeco.dayzee.webService.repo.DayzeeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TimenoteViewModel: ViewModel() {

    private val timenoteService = DayzeeRepository().getTimenoteService()

    fun getRecentTimenotePagingFlow(token: String, sharedPreferences: SharedPreferences) : Flow<PagingData<TimenoteInfoDTO>> = Pager(PagingConfig(pageSize = 1)){ TimenoteRecentPagingSource(token, timenoteService, sharedPreferences)}.flow.cachedIn(viewModelScope)
    fun getUpcomingTimenotePagingFlow(token: String, upcoming: Boolean, sharedPreferences: SharedPreferences) = Pager(PagingConfig(pageSize = 2)){ TimenotePagingSource(token, timenoteService, upcoming, sharedPreferences) }.flow.cachedIn(viewModelScope)
    fun getAroundTimenotePagingFlow(token: String, filterLocationDTO: FilterLocationDTO, sharedPreferences: SharedPreferences) = Pager(PagingConfig(pageSize = 1)){ TimenoteAroundPagingSource(token, timenoteService, filterLocationDTO, sharedPreferences) }.flow.cachedIn(viewModelScope)
    fun getSpecificTimenote(token: String, id: String) = flow { emit(timenoteService.getTimenoteId("Bearer $token",id)) }.asLiveData(viewModelScope.coroutineContext)
    fun modifySpecificTimenote(token: String, id: String, timenoteBody: CreationTimenoteDTO) =  flow {emit(timenoteService.modifyTimenote("Bearer $token",id, timenoteBody))}.asLiveData(viewModelScope.coroutineContext)
    fun deleteTimenote(token: String, id: String) = flow {emit(timenoteService.deleteTimenote("Bearer $token",id))}.asLiveData(viewModelScope.coroutineContext)
    fun joinTimenote(token: String, id: String) = flow { emit(timenoteService.joinTimenote("Bearer $token",id)) }.asLiveData(viewModelScope.coroutineContext)
    fun leaveTimenote(token: String, id: String) = flow { emit(timenoteService.leaveTimenote("Bearer $token",id)) }.asLiveData(viewModelScope.coroutineContext)
    fun hideToOthers(token: String, id: String) = flow { emit(timenoteService.joinPrivateTimenote("Bearer $token",id)) }.asLiveData(viewModelScope.coroutineContext)
    fun signalTimenote(token: String, timenoteCreationSignalementDTO: TimenoteCreationSignalementDTO) = flow { emit(timenoteService.signaleTimenote("Bearer $token",timenoteCreationSignalementDTO)) }.asLiveData(viewModelScope.coroutineContext)
    fun createTimenote(token: String, creationTimenoteDTO: CreationTimenoteDTO) = flow { emit(timenoteService.createTimenote("Bearer $token", creationTimenoteDTO)) }.asLiveData(viewModelScope.coroutineContext)
    fun bulkTimenote(token: String, creationTimenoteDTO: List<CreationTimenoteDTO>) = flow { emit(timenoteService.bulkTimenote("Bearer $token", creationTimenoteDTO)) }.asLiveData(viewModelScope.coroutineContext)
    fun getUsersParticipating(token: String, id: String, sharedPreferences: SharedPreferences) = Pager(PagingConfig(pageSize = 1)){UsersParticipatingPagingSource(token, id, timenoteService, sharedPreferences)}.flow.cachedIn(viewModelScope)
    fun shareWith(token: String, shareTimenoteDTO: ShareTimenoteDTO) = flow { emit(timenoteService.shareWith("Bearer $token", shareTimenoteDTO)) }.asLiveData(viewModelScope.coroutineContext)


}