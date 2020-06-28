package com.timenoteco.timenote.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel: ViewModel() {

    enum class AuthenticationState {
        UNAUTHENTICATED,
        AUTHENTICATED  ,
        INVALID_AUTHENTICATION,
        UNAUTHENTICATED_CHOOSED
    }

    val authenticationState = MutableLiveData<AuthenticationState>()
    var username: String

    init {
        authenticationState.value = AuthenticationState.UNAUTHENTICATED
        username = ""
    }

    fun refuseAuthentication() {
        authenticationState.value = AuthenticationState.UNAUTHENTICATED
    }

    fun authenticate(username: String, password: String) {
        if (passwordIsValidForUsername(username, password)) {
            this.username = username
            authenticationState.value = AuthenticationState.AUTHENTICATED
        } else {
            authenticationState.value = AuthenticationState.INVALID_AUTHENTICATION
        }
    }

    private fun passwordIsValidForUsername(username: String, password: String): Boolean {
        return username == "sam"
    }

}