package com.timenoteco.timenote.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.timenoteco.timenote.model.FCMDTO
import com.timenoteco.timenote.webService.repo.DayzeeRepository
import kotlinx.coroutines.flow.flow

class MeViewModel: ViewModel() {

    private val meService = DayzeeRepository().getMeService()

    fun putFCMToken(token: String, fcmToken: FCMDTO) = flow { emit(meService.putFCMToken("Bearer $token", fcmToken)) }.asLiveData(viewModelScope.coroutineContext)
}