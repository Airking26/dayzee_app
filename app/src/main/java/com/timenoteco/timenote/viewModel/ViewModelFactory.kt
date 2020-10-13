package com.timenoteco.timenote.viewModel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import androidx.lifecycle.ViewModelProvider
import kotlin.reflect.full.primaryConstructor


class ViewModelFactory(private vararg val args: Any) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        modelClass.kotlin.primaryConstructor?.call(*args)
            ?: throw IllegalArgumentException("$modelClass primaryConstructor is null")

}

class StringViewModel : ViewModel(){

    private val switchNotif = MutableLiveData<Boolean>()

    fun switchNotif(boolean: Boolean): LiveData<Boolean>{
        switchNotif.postValue(boolean)
        return switchNotif
    }

    fun getSwitchNotifLiveData(): LiveData<Boolean> {
        return switchNotif
    }

}
