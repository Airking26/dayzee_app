package com.timenoteco.timenote.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.timenoteco.timenote.model.DetailedPlace
import com.timenoteco.timenote.model.NearbyRequestBody
import com.timenoteco.timenote.paging.NearbyPagingSource
import com.timenoteco.timenote.paging.TimenoteRemotePagingSource
import com.timenoteco.timenote.webService.repo.DayzeeRepository
import com.timenoteco.timenote.webService.repo.PlaceRepository
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class NearbyViewModel: ViewModel() {

    private val placeService = PlaceRepository().getPlaceService()
    private val nearbyService = DayzeeRepository().getNearbyService()

    fun fetchLocation(id : String): LiveData<Response<DetailedPlace>> {
        return flow {
            emit(placeService.getDetailedPlace(id, "AIzaSyBhM9HQo1fzDlwkIVqobfmrRmEMCWTU1CA"))
        }.asLiveData(viewModelScope.coroutineContext)
    }

    fun getNearbyResults(token: String, nearbyRequestBody: NearbyRequestBody) = Pager(PagingConfig(pageSize = 1)){NearbyPagingSource("Bearer $token", nearbyRequestBody, nearbyService)}.flow.cachedIn(viewModelScope)

}