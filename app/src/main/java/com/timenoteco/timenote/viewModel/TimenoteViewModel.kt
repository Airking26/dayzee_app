package com.timenoteco.timenote.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.timenoteco.timenote.model.TimenoteBody
import com.timenoteco.timenote.paging.TimenoteRemotePagingSource
import com.timenoteco.timenote.webService.repo.DayzeeRepository
import kotlinx.coroutines.flow.flow

class TimenoteViewModel: ViewModel() {

    private val timenoteService = DayzeeRepository().getTimenoteService()

    fun getTimenotePagingFlow() = Pager(PagingConfig(pageSize = 12)){ TimenoteRemotePagingSource(timenoteService) }.flow.cachedIn(viewModelScope)
    fun getSpecificTimenote(id: String) = flow { emit(timenoteService.getTimenoteId("Bearer " + "",id)) }.asLiveData(viewModelScope.coroutineContext)
    fun modifySpecificTimenote(id: String, timenoteBody: TimenoteBody) =  flow {emit(timenoteService.modifyTimenote("Bearer " + "",id, timenoteBody))}.asLiveData(viewModelScope.coroutineContext)
    fun deleteTimenote(id: String) = flow {emit(timenoteService.deleteTimenote("Bearer " + "",id))}.asLiveData(viewModelScope.coroutineContext)
    fun joinTimenote(id: String) = flow { emit(timenoteService.joinTimenote("Bearer " + "",id)) }.asLiveData(viewModelScope.coroutineContext)
    fun leaveTimenote(id: String) = flow { emit(timenoteService.leaveTimenote("Bearer " + "",id)) }.asLiveData(viewModelScope.coroutineContext)
    fun hideToOthers(id: String) = flow { emit(timenoteService.joinPrivateTimenote("Bearer " + "",id)) }.asLiveData(viewModelScope.coroutineContext)

}