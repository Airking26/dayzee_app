package com.timenoteco.timenote.viewModel

import androidx.lifecycle.*
import com.timenoteco.timenote.model.UserSignInBody
import com.timenoteco.timenote.model.UserSignUpBody
import com.timenoteco.timenote.webService.repo.DayzeeRepository
import kotlinx.coroutines.flow.flow

class LoginViewModel: ViewModel() {

    enum class AuthenticationState {
        UNAUTHENTICATED,
        AUTHENTICATED  ,
        INVALID_AUTHENTICATION,
        GUEST
    }

    private val authService = DayzeeRepository()
        .getAuthService()
    val authenticationState = MutableLiveData<AuthenticationState>()

    init {
        authenticationState.value = AuthenticationState.UNAUTHENTICATED
    }

    fun refuseAuthentication() {
        authenticationState.value = AuthenticationState.UNAUTHENTICATED
    }

    fun authenticate(username: String, password: String) {
        if (passwordIsValidForUsername(username, password).value == 201) {
            authenticationState.value = AuthenticationState.AUTHENTICATED
        } else {
            authenticationState.value = AuthenticationState.INVALID_AUTHENTICATION
        }
    }

    private fun passwordIsValidForUsername(username: String, password: String): LiveData<Int> {
        return flow {
            emit(authService.signIn(UserSignInBody(username, password)).code())
        }.asLiveData(viewModelScope.coroutineContext)
    }

    fun addUser(userSignUpBody: UserSignUpBody){
        when (checkAddUser(userSignUpBody).value) {
            201 -> authenticationState.postValue(AuthenticationState.AUTHENTICATED)
            409 -> authenticationState.postValue(AuthenticationState.INVALID_AUTHENTICATION)
            else -> authenticationState.postValue(AuthenticationState.UNAUTHENTICATED)
        }
    }

    private fun checkAddUser(userSignUpBody: UserSignUpBody): LiveData<Int> {
        return flow {
            emit(authService.signUp(userSignUpBody).code())
        }.asLiveData(viewModelScope.coroutineContext)
    }

}