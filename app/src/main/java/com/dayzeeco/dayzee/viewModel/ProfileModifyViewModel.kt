package com.dayzeeco.dayzee.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dayzeeco.dayzee.model.DetailedPlace
import com.dayzeeco.dayzee.model.UpdateUserInfoDTO
import com.dayzeeco.dayzee.webService.repo.DayzeeRepository
import com.dayzeeco.dayzee.webService.repo.PlaceRepository
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class ProfileModifyViewModel: ViewModel() {

    private val placeService = PlaceRepository().getPlaceService()

    fun fetchLocation(id : String): LiveData<Response<DetailedPlace>> {
        return flow {
            emit(placeService.getDetailedPlace(id, "AIzaSyDozXNaca8OBwQO5hHQ_pdGYJ9IZmJTXQs"))
        }.asLiveData(viewModelScope.coroutineContext)
    }
}