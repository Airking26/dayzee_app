package com.timenoteco.timenote.viewModel

import android.text.TextUtils
import android.util.Patterns
import androidx.lifecycle.*
import com.timenoteco.timenote.model.*
import com.timenoteco.timenote.webService.repo.DayzeeRepository
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class LoginViewModel: ViewModel() {

    enum class AuthenticationState {
        UNAUTHENTICATED,
        AUTHENTICATED,
        GUEST
    }

    private val authService = DayzeeRepository().getAuthService()
    private val authenticationState = MutableLiveData<AuthenticationState>()

    init {
        markAsAuthenticated()
    }

    fun getAuthenticationState() : LiveData<AuthenticationState> = authenticationState

    fun refuseAuthentication() = authenticationState.postValue(AuthenticationState.UNAUTHENTICATED)

    fun login(username: String, password: String, isEmail: Boolean): LiveData<Response<RootUserResponse>> {
        return if(isEmail) {
            flow {
                emit(authService.signInEmail(UserEmailSignInBody(username, password)))
            }.asLiveData(viewModelScope.coroutineContext)
        } else {
            flow {
                emit(authService.signInUsername(UserUserNameSignInBody(username, password)))
            }.asLiveData(viewModelScope.coroutineContext)
        }
    }

    fun checkAddUser(userSignUpBody: UserSignUpBody): LiveData<Response<RootUserResponse>> {
        return flow {
            emit(authService.signUp(userSignUpBody))
        }.asLiveData(viewModelScope.coroutineContext)
    }

    fun isValidEmail(target: String?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target!!).matches()
    }

    fun isValidUsername(username: String): Boolean{
        return username.contains('@')
    }

    fun checkIfEmailAvailable(email: String): LiveData<Response<IsAvailable>> {
        return flow{
            val o = authService.checkEmailAvailability(email)
            emit(authService.checkEmailAvailability(email))
        }.asLiveData(viewModelScope.coroutineContext)
    }

    fun checkIfUsernameAvailable(username: String): LiveData<Response<IsAvailable>> {
        return flow{
         emit(authService.checkUsernameAvailability(username))
        }.asLiveData(viewModelScope.coroutineContext)
    }

    fun markAsGuest() = authenticationState.postValue(AuthenticationState.GUEST)
    fun markAsAuthenticated() = authenticationState.postValue(AuthenticationState.AUTHENTICATED)
    fun markAsUnauthenticated() = authenticationState.postValue(AuthenticationState.UNAUTHENTICATED)

}