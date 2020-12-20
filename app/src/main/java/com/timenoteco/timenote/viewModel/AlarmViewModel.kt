package com.timenoteco.timenote.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.timenoteco.timenote.model.AlarmCreationDTO
import com.timenoteco.timenote.webService.repo.DayzeeRepository
import kotlinx.coroutines.flow.flow

class AlarmViewModel : ViewModel() {

    private val alarmService = DayzeeRepository().getAlarmService()

    fun getAlarms(token: String) = flow { emit(alarmService.getAlarms("Bearer $token")) }.asLiveData(viewModelScope.coroutineContext)
    fun createAlarm(token: String, alarmCreationDTO: AlarmCreationDTO) = flow { emit(alarmService.createAlarm("Bearer $token", alarmCreationDTO))}.asLiveData(viewModelScope.coroutineContext)
    fun updateAlarm(token: String, id: String, alarmCreationDTO: AlarmCreationDTO) = flow { emit(alarmService.updateAlarm("Bearer $token", id, alarmCreationDTO))}.asLiveData(viewModelScope.coroutineContext)
    fun deleteAlarm(token: String, id: String) = flow { emit(alarmService.deleteAlarm("Bearer $token", id))}.asLiveData(viewModelScope.coroutineContext)
}