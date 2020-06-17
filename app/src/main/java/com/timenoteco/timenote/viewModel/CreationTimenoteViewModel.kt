package com.timenoteco.timenote.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.timenoteco.timenote.model.Timenote
import com.timenoteco.timenote.repository.CreationTimenoteData

class CreationTimenoteViewModel: ViewModel() {

    private val timenoteLiveData = MutableLiveData<Timenote>()

    fun setDescription(description : String){
        timenoteLiveData.value = CreationTimenoteData().setDescription(description)
    }

    fun getDescription(): String?{
        return timenoteLiveData.value?.title
    }

}