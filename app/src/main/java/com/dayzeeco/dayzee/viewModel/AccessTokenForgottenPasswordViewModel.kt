package com.dayzeeco.dayzee.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AccessTokenForgottenPasswordViewModel : ViewModel(){

    private val accessTok = MutableLiveData<String>()

    fun setAccessTok(boolean: String): LiveData<String>{
        accessTok.postValue(boolean)
        return accessTok
    }

    fun getAccessTok(): LiveData<String> {
        return accessTok
    }

}
