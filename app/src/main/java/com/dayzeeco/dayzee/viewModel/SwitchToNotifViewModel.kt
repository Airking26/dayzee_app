package com.dayzeeco.dayzee.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SwitchToNotifViewModel : ViewModel(){

    private val switchNotif = MutableLiveData<Boolean>()

    fun switchNotif(boolean: Boolean): LiveData<Boolean>{
        switchNotif.postValue(boolean)
        return switchNotif
    }

    fun getSwitchNotifLiveData(): LiveData<Boolean> {
        return switchNotif
    }

}
