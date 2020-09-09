package com.timenoteco.timenote.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.timenoteco.timenote.model.DetailedPlace
import com.timenoteco.timenote.model.UpdateUserInfoDTO
import com.timenoteco.timenote.webService.repo.DayzeeRepository
import com.timenoteco.timenote.webService.repo.PlaceRepository
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class ProfileModifyViewModel: ViewModel() {

    private val meService = DayzeeRepository().getMeService()
    private val placeService = PlaceRepository().getPlaceService()

    fun modifyProfile(token: String, updateUserInfo: UpdateUserInfoDTO) = flow { emit(meService.modifyMyInfos("Bearer $token", updateUserInfo)) }.asLiveData(viewModelScope.coroutineContext)

    fun fetchLocation(id : String): LiveData<Response<DetailedPlace>> {
        return flow {
            emit(placeService.getDetailedPlace(id, "AIzaSyBhM9HQo1fzDlwkIVqobfmrRmEMCWTU1CA"))
        }.asLiveData(viewModelScope.coroutineContext)
    }
}