package com.timenoteco.timenote.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.timenoteco.timenote.model.TimenoteInfoDTO
import com.timenoteco.timenote.model.UserInfoDTO
import com.timenoteco.timenote.paging.ProfileEventPagingSource
import com.timenoteco.timenote.paging.UserPagingSource
import com.timenoteco.timenote.webService.repo.DayzeeRepository
import kotlinx.coroutines.flow.Flow

class ProfileViewModel: ViewModel() {

    private val profileService = DayzeeRepository().getProfileService()
    private val followService = DayzeeRepository().getFollowService()
    private val timenoteService = DayzeeRepository().getTimenoteService()

    fun getUsers(token: String, followers: Boolean, useTimenoteService: Boolean, id: String?): Flow<PagingData<UserInfoDTO>> {
        return Pager(PagingConfig(pageSize = 8)){UserPagingSource(token, followService, followers, timenoteService, useTimenoteService, id)}.flow.cachedIn(viewModelScope)
    }

    fun getFutureTimenotes(future: Boolean): Flow<PagingData<TimenoteInfoDTO>> {
        return Pager(PagingConfig(pageSize = 9)){ProfileEventPagingSource(profileService, future)}.flow.cachedIn(viewModelScope)
    }

}