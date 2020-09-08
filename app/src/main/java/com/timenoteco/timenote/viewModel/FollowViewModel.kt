package com.timenoteco.timenote.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.timenoteco.timenote.paging.FollowPagingSource
import com.timenoteco.timenote.webService.repo.DayzeeRepository
import kotlinx.coroutines.flow.flow

class FollowViewModel: ViewModel() {

    private val followService = DayzeeRepository().getFollowService()

    fun followPublicUser(token: String, id: Int) = flow { emit(followService.followPublicUser("Bearer $token", id)) }.asLiveData(viewModelScope.coroutineContext)
    fun followPrivateUser(token: String, id: Int) = flow { emit(followService.followPrivateUser("Bearer $token", id))}.asLiveData(viewModelScope.coroutineContext)
    fun acceptFollowingRequest(token: String, id: Int) = flow { emit(followService.acceptFollowingRequest("Bearer $token", id))}.asLiveData(viewModelScope.coroutineContext)
    fun declineFollowingRequest(token: String, id: Int) = flow { emit(followService.declineFollowingRequest("Bearer $token", id))}.asLiveData(viewModelScope.coroutineContext)
    fun unfollowUser(token: String, id: Int) = flow { emit(followService.unfollowUser("Bearer $token", id))}.asLiveData(viewModelScope.coroutineContext)
    fun removeUserFromFollower(token: String, id: Int) = flow { emit(followService.removeUserFromFollowers("Bearer $token", id))}.asLiveData(viewModelScope.coroutineContext)
    fun getUsersWaitingForApproval(token: String) = Pager(PagingConfig(pageSize = 12)){FollowPagingSource(token, followService, true)}.flow.cachedIn(viewModelScope)
    fun getUsersAskedToFollow(token: String) = Pager(PagingConfig(pageSize = 12)){FollowPagingSource(token, followService, false)}.flow.cachedIn(viewModelScope)

}