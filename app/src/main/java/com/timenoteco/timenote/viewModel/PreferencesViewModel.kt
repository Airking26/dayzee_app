package com.timenoteco.timenote.viewModel

import androidx.lifecycle.*
import com.timenoteco.timenote.model.Category
import com.timenoteco.timenote.model.Preferences
import com.timenoteco.timenote.model.SubCategoryRated
import com.timenoteco.timenote.webService.repo.DayzeeRepository
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class PreferencesViewModel: ViewModel() {

    private val preferencesService = DayzeeRepository().getPreferencesService()

    fun getPreferences(token: String): LiveData<Response<MutableList<SubCategoryRated>>> {
        return flow {
            emit(preferencesService.getMyPreferences("Bearer $token"))
        }.asLiveData(viewModelScope.coroutineContext)
    }

    fun getCategories(): LiveData<Response<List<Category>>> {
        return flow {emit(preferencesService.getAllCategories())}.asLiveData(viewModelScope.coroutineContext)
    }

    fun modifyPreferences(token: String, preferences: Preferences): LiveData<Response<List<SubCategoryRated>>> {
        return flow {
            emit(preferencesService.modifyPreferences("Bearer $token", preferences))
        }.asLiveData(viewModelScope.coroutineContext)
    }
}