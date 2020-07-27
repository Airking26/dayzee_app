package com.timenoteco.timenote.viewModel

import androidx.lifecycle.*
import com.timenoteco.timenote.model.RootUserResponse
import com.timenoteco.timenote.model.UserSignInBody
import com.timenoteco.timenote.model.UserSignUpBody
import com.timenoteco.timenote.webService.repo.DayzeeRepository
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class LoginViewModel: ViewModel() {

    enum class AuthenticationState {
        UNAUTHENTICATED,
        AUTHENTICATED  ,
        INVALID_AUTHENTICATION,
        GUEST
    }

    private val authService = DayzeeRepository().getAuthService()
    private val authenticationState = MutableLiveData<AuthenticationState>()

    fun getAuthenticationState() : LiveData<AuthenticationState>{
        return authenticationState
    }

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

    fun checkAddUser(userSignUpBody: UserSignUpBody): LiveData<Response<RootUserResponse>> {
        return flow {
            emit(authService.signUp(userSignUpBody))
        }.asLiveData(viewModelScope.coroutineContext)
    }

    fun markAsGuest() {
        authenticationState.postValue(AuthenticationState.GUEST)
    }

    fun markAsAuthenticated() {
        authenticationState.postValue(AuthenticationState.AUTHENTICATED)
    }

    fun markAsInvalidAuthentication() {
        authenticationState.postValue(AuthenticationState.INVALID_AUTHENTICATION)
    }

}