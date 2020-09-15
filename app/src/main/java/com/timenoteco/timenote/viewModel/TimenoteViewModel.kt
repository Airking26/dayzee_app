package com.timenoteco.timenote.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.timenoteco.timenote.model.CreationTimenoteDTO
import com.timenoteco.timenote.model.ShareTimenoteDTO
import com.timenoteco.timenote.model.TimenoteBody
import com.timenoteco.timenote.paging.TimenoteRecentPagingSource
import com.timenoteco.timenote.paging.TimenoteRemotePagingSource
import com.timenoteco.timenote.paging.UsersParticipatingPagingSource
import com.timenoteco.timenote.webService.repo.DayzeeRepository
import kotlinx.coroutines.flow.flow

class TimenoteViewModel: ViewModel() {

    private val timenoteService = DayzeeRepository().getTimenoteService()

    fun getRecentTimenotePagingFlow(token: String) = Pager(PagingConfig(pageSize = 1)){ TimenoteRecentPagingSource(token, timenoteService)}.flow.cachedIn(viewModelScope)
    fun getTimenotePagingFlow(token: String) = Pager(PagingConfig(pageSize = 1)){ TimenoteRemotePagingSource(token, timenoteService) }.flow.cachedIn(viewModelScope)
    fun getSpecificTimenote(token: String, id: String) = flow { emit(timenoteService.getTimenoteId("Bearer $token",id)) }.asLiveData(viewModelScope.coroutineContext)
    fun modifySpecificTimenote(token: String, id: String, timenoteBody: CreationTimenoteDTO) =  flow {emit(timenoteService.modifyTimenote("Bearer $token",id, timenoteBody))}.asLiveData(viewModelScope.coroutineContext)
    fun deleteTimenote(token: String, id: String) = flow {emit(timenoteService.deleteTimenote("Bearer $token",id))}.asLiveData(viewModelScope.coroutineContext)
    fun joinTimenote(token: String, id: String) = flow { emit(timenoteService.joinTimenote("Bearer $token",id)) }.asLiveData(viewModelScope.coroutineContext)
    fun leaveTimenote(token: String, id: String) = flow { emit(timenoteService.leaveTimenote("Bearer $token",id)) }.asLiveData(viewModelScope.coroutineContext)
    fun hideToOthers(token: String, id: String) = flow { emit(timenoteService.joinPrivateTimenote("Bearer $token",id)) }.asLiveData(viewModelScope.coroutineContext)
    fun createTimenote(token: String, creationTimenoteDTO: CreationTimenoteDTO) = flow { emit(timenoteService.createTimenote("Bearer $token", creationTimenoteDTO)) }.asLiveData(viewModelScope.coroutineContext)
    fun getUsersParticipating(token: String, id: String) = Pager(PagingConfig(pageSize = 1)){UsersParticipatingPagingSource(token, id, timenoteService)}.flow.cachedIn(viewModelScope)
    fun shareWith(token: String, shareTimenoteDTO: ShareTimenoteDTO) = flow { emit(timenoteService.shareWith("Bearer: $token", shareTimenoteDTO)) }.asLiveData(viewModelScope.coroutineContext)


}