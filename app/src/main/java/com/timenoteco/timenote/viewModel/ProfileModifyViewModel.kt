package com.timenoteco.timenote.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.timenoteco.timenote.model.ProfilModifyModel
import com.timenoteco.timenote.webService.repo.DayzeeRepository
import kotlinx.coroutines.flow.flow

class ProfileModifyViewModel: ViewModel() {

    private val profileModifyService = DayzeeRepository().getProfileModifyService()

    fun modifyProfile(token: String, profileModifyModel: ProfilModifyModel) = flow { emit(profileModifyService.modifyProfile(token, profileModifyModel)) }.asLiveData(viewModelScope.coroutineContext)

}