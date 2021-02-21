package com.dayzeeco.dayzee.viewModel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.dayzeeco.dayzee.model.DetailedPlace
import com.dayzeeco.dayzee.model.NearbyRequestBody
import com.dayzeeco.dayzee.paging.NearbyPagingSource
import com.dayzeeco.dayzee.webService.repo.DayzeeRepository
import com.dayzeeco.dayzee.webService.repo.PlaceRepository
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class NearbyViewModel: ViewModel() {

    private val placeService = PlaceRepository().getPlaceService()
    private val nearbyService = DayzeeRepository().getNearbyService()

    fun fetchLocation(id : String, apiKey: String): LiveData<Response<DetailedPlace>> {
        return flow { emit(placeService.getDetailedPlace(id, apiKey)) }.asLiveData(viewModelScope.coroutineContext)
    }

    fun getNearbyResults(nearbyRequestBody: NearbyRequestBody, sharedPreferences: SharedPreferences) = Pager(PagingConfig(pageSize = 1)){NearbyPagingSource(nearbyRequestBody, nearbyService, sharedPreferences)}.flow.cachedIn(viewModelScope)

}