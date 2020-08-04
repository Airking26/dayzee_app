package com.timenoteco.timenote.viewModel

import android.text.TextUtils
import android.util.Patterns
import androidx.lifecycle.*
import com.timenoteco.timenote.model.RootUserResponse
import com.timenoteco.timenote.model.UserEmailSignInBody
import com.timenoteco.timenote.model.UserSignUpBody
import com.timenoteco.timenote.model.UserUserNameSignInBody
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

    fun getAuthenticationState() : LiveData<AuthenticationState> = authenticationState

    init { authenticationState.value = AuthenticationState.AUTHENTICATED }

    fun refuseAuthentication() = authenticationState.postValue(AuthenticationState.UNAUTHENTICATED)

    fun authenticate(username: String, password: String) {
        if (passwordIsValidForUsername(username, password, isValidEmail(username)).value == 201) {
            authenticationState.value = AuthenticationState.AUTHENTICATED
        } else {
            authenticationState.value = AuthenticationState.INVALID_AUTHENTICATION
        }
    }

    private fun passwordIsValidForUsername(username: String, password: String, isEmail: Boolean): LiveData<Int> {
        return if(isEmail) {
            flow {
                emit(authService.signInEmail(UserEmailSignInBody(username, password)).code())
            }.asLiveData(viewModelScope.coroutineContext)
        } else {
            flow {
                emit(authService.signInUsername(UserUserNameSignInBody(username, password)).code())
            }.asLiveData(viewModelScope.coroutineContext)
        }
    }

    fun checkAddUser(userSignUpBody: UserSignUpBody): LiveData<Response<RootUserResponse>> {
        return flow {
            emit(authService.signUp(userSignUpBody))
        }.asLiveData(viewModelScope.coroutineContext)
    }

    fun isValidEmail(target: String?): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target!!)
            .matches()
    }

    fun checkIfEmailAvailable(email: String): LiveData<Boolean>{
        return flow{
         emit(authService.checkEmailAvailability(email))
        }.asLiveData(viewModelScope.coroutineContext)
    }

    fun checkIfUsernameAvailable(username: String): LiveData<Boolean>{
        return flow{
         emit(authService.checkUsernameAvailability(username))
        }.asLiveData(viewModelScope.coroutineContext)
    }

    fun markAsGuest() = authenticationState.postValue(AuthenticationState.GUEST)
    fun markAsAuthenticated() = authenticationState.postValue(AuthenticationState.AUTHENTICATED)
    fun markAsInvalidAuthentication() = authenticationState.postValue(AuthenticationState.INVALID_AUTHENTICATION)

}