package com.dayzeeco.dayzee.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dayzeeco.dayzee.model.TimenoteHiddedCreationDTO
import com.dayzeeco.dayzee.webService.repo.DayzeeRepository
import com.dayzeeco.dayzee.webService.service.TimenoteHiddedService
import kotlinx.coroutines.flow.flow

class TimenoteHiddedViewModel: ViewModel() {

    private val timenoteHiddedService: TimenoteHiddedService = DayzeeRepository().getTimenoteHiddedService()

    fun hideEventOrUSer(token: String, timenoteHiddedCreationDTO: TimenoteHiddedCreationDTO) = flow { emit(timenoteHiddedService.hideEventOrUser("Bearer $token", timenoteHiddedCreationDTO)) }.asLiveData(viewModelScope.coroutineContext)
}