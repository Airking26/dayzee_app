package com.timenoteco.timenote.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.timenoteco.timenote.paging.TimenoteRemotePagingSource
import com.timenoteco.timenote.webService.repo.DayzeeRepository

class TimenoteViewModel: ViewModel() {

    private val timenoteService = DayzeeRepository().getTimenoteService()

    val timenotePagingFlow = Pager(PagingConfig(pageSize = 12)){
        TimenoteRemotePagingSource(timenoteService)
    }.flow.cachedIn(viewModelScope)

}