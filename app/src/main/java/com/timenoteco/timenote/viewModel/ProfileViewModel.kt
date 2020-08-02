package com.timenoteco.timenote.viewModel

import androidx.lifecycle.ViewModel
import com.timenoteco.timenote.webService.repo.DayzeeRepository

class ProfileViewModel: ViewModel() {

    private val profileService = DayzeeRepository().getProfileService()


}