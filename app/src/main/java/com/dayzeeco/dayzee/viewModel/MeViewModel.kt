package com.dayzeeco.dayzee.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dayzeeco.dayzee.model.FCMDTO
import com.dayzeeco.dayzee.model.UpdateUserInfoDTO
import com.dayzeeco.dayzee.webService.repo.DayzeeRepository
import kotlinx.coroutines.flow.flow

class MeViewModel: ViewModel() {

    private val meService = DayzeeRepository().getMeService()

    fun putFCMToken(token: String, fcmToken: FCMDTO) = flow { emit(meService.putFCMToken("Bearer $token", fcmToken)) }.asLiveData(viewModelScope.coroutineContext)
    fun getSpecificUser(token: String, id: String) = flow { emit(meService.getSpecificUser("Bearer $token", id))}.asLiveData(viewModelScope.coroutineContext)
    fun modifyProfile(token: String, updateUserInfo: UpdateUserInfoDTO) = flow { emit(meService.modifyMyInfos("Bearer $token", updateUserInfo)) }.asLiveData(viewModelScope.coroutineContext)
    fun getMyProfile(token: String) = flow { emit(meService.getMyInfos("Bearer $token")) }.asLiveData(viewModelScope.coroutineContext)
    fun changePassword(token: String, password: String) = flow { emit(meService.changePassword("Bearer $token", password)) }.asLiveData(viewModelScope.coroutineContext)
}