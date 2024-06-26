package com.dayzeeco.dayzee.viewModel

import androidx.lifecycle.*
import com.dayzeeco.dayzee.model.Category
import com.dayzeeco.dayzee.model.Preferences
import com.dayzeeco.dayzee.model.SubCategoryRated
import com.dayzeeco.dayzee.webService.repo.DayzeeRepository
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class PreferencesViewModel: ViewModel() {

    private val preferencesService = DayzeeRepository().getPreferencesService()

    fun getPreferences(token: String) = flow { emit(preferencesService.getMyPreferences("Bearer $token")) }.asLiveData(viewModelScope.coroutineContext)
    fun getCategories() = flow {emit(preferencesService.getAllCategories())}.asLiveData(viewModelScope.coroutineContext)
    fun modifyPreferences(token: String, preferences: Preferences) = flow { emit(preferencesService.modifyPreferences("Bearer $token", preferences)) }.asLiveData(viewModelScope.coroutineContext)
}