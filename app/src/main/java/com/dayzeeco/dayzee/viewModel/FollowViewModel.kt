package com.dayzeeco.dayzee.viewModel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dayzeeco.dayzee.model.UserInfoDTO
import com.dayzeeco.dayzee.paging.FollowPagingSource
import com.dayzeeco.dayzee.paging.FollowSearchUserPagingSource
import com.dayzeeco.dayzee.paging.UserPagingSource
import com.dayzeeco.dayzee.webService.repo.DayzeeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FollowViewModel: ViewModel() {

    private val followService = DayzeeRepository().getFollowService()

    fun followPublicUser(token: String, id: String) = flow { emit(followService.followPublicUser("Bearer $token", id)) }.asLiveData(viewModelScope.coroutineContext)
    fun checkUserWaitingApproval(token: String, id: String) = flow { emit(followService.checkUserWaitingApproval("Bearer $token", id)) }.asLiveData(viewModelScope.coroutineContext)
    fun followPrivateUser(token: String, id: String) = flow { emit(followService.followPrivateUser("Bearer $token", id))}.asLiveData(viewModelScope.coroutineContext)
    fun acceptFollowingRequest(token: String, id: String) = flow { emit(followService.acceptFollowingRequest("Bearer $token", id))}.asLiveData(viewModelScope.coroutineContext)
    fun declineFollowingRequest(token: String, id: String) = flow { emit(followService.declineFollowingRequest("Bearer $token", id))}.asLiveData(viewModelScope.coroutineContext)
    fun unfollowUser(token: String, id: String) = flow { emit(followService.unfollowUser("Bearer $token", id))}.asLiveData(viewModelScope.coroutineContext)
    fun removeUserFromFollower(token: String, id: String) = flow { emit(followService.removeUserFromFollowers("Bearer $token", id))}.asLiveData(viewModelScope.coroutineContext)
    fun getUsersWaitingForApproval(token: String, sharedPreferences: SharedPreferences) = Pager(PagingConfig(pageSize = 1)){FollowPagingSource(token, followService, true, sharedPreferences)}.flow.cachedIn(viewModelScope)
    fun getUsersAskedToFollow(token: String, sharedPreferences: SharedPreferences) = Pager(PagingConfig(pageSize = 1)){FollowPagingSource(token, followService, false, sharedPreferences)}.flow.cachedIn(viewModelScope)
    fun getUsers(token: String, id: String, followers: Int, sharedPreferences: SharedPreferences): Flow<PagingData<UserInfoDTO>>  = Pager(PagingConfig(pageSize = 1)){ UserPagingSource(token, id, followService, followers, sharedPreferences) }.flow.cachedIn(viewModelScope)
    fun searchInFollowing(token: String, search: String, sharedPreferences: SharedPreferences) = Pager(PagingConfig(pageSize = 1)){FollowSearchUserPagingSource(token, search, followService, sharedPreferences)}.flow.cachedIn(viewModelScope)

}