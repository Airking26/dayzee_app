package com.timenoteco.timenote.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.timenoteco.timenote.model.UpdateUserInfoDTO
import com.timenoteco.timenote.webService.repo.DayzeeRepository
import kotlinx.coroutines.flow.flow

class ProfileModifyViewModel: ViewModel() {

    private val meService = DayzeeRepository().getMeService()

    fun modifyProfile(token: String, updateUserInfo: UpdateUserInfoDTO) = flow { emit(meService.modifyMyInfos("Bearer $token", updateUserInfo)) }.asLiveData(viewModelScope.coroutineContext)

}