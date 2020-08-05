package com.timenoteco.timenote.viewModel

import androidx.lifecycle.ViewModel
import com.timenoteco.timenote.webService.repo.DayzeeRepository

class FollowViewModel: ViewModel() {

    private val followService = DayzeeRepository().getFollowService()


}