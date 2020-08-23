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

    fun getTimenotePagingFlow(token: String) =
        Pager(PagingConfig(pageSize = 12)){ TimenoteRemotePagingSource(token, timenoteService) }.flow.cachedIn(viewModelScope)
    fun getSpecificTimenote(token: String, id: String) = flow { emit(timenoteService.getTimenoteId("Bearer $token",id)) }.asLiveData(viewModelScope.coroutineContext)
    fun modifySpecificTimenote(token: String, id: String, timenoteBody: TimenoteBody) =  flow {emit(timenoteService.modifyTimenote("Bearer $token",id, timenoteBody))}.asLiveData(viewModelScope.coroutineContext)
    fun deleteTimenote(token: String, id: String) = flow {emit(timenoteService.deleteTimenote("Bearer $token",id))}.asLiveData(viewModelScope.coroutineContext)
    fun joinTimenote(token: String, id: String) = flow { emit(timenoteService.joinTimenote("Bearer $token",id)) }.asLiveData(viewModelScope.coroutineContext)
    fun leaveTimenote(token: String, id: String) = flow { emit(timenoteService.leaveTimenote("Bearer $token",id)) }.asLiveData(viewModelScope.coroutineContext)
    fun hideToOthers(token: String, id: String) = flow { emit(timenoteService.joinPrivateTimenote("Bearer $token",id)) }.asLiveData(viewModelScope.coroutineContext)

}