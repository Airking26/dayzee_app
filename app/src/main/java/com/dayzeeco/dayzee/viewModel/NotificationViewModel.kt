package com.dayzeeco.dayzee.viewModel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.dayzeeco.dayzee.paging.NotificationPagingSource
import com.dayzeeco.dayzee.webService.repo.DayzeeRepository
import kotlinx.coroutines.flow.flow

class NotificationViewModel: ViewModel() {

    private val notificationService = DayzeeRepository().getNotificationService()

    fun getNotifications(token: String, id : String, sharedPreferences: SharedPreferences) = Pager(PagingConfig(pageSize = 1)){ NotificationPagingSource(token, id, notificationService, sharedPreferences) }.flow.cachedIn(viewModelScope)
    fun checkUnreadNotifications(token: String, id: String) = flow { emit(notificationService.getNotifications("Bearer $token", id, 0)) }.asLiveData(viewModelScope.coroutineContext)
    fun deleteNotification(token: String, id: String) = flow {emit(notificationService.deleteNotification("Bearer $token", id))}.asLiveData(viewModelScope.coroutineContext)


}