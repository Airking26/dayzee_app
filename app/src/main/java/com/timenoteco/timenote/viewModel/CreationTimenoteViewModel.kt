package com.timenoteco.timenote.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.timenoteco.timenote.model.Timenote
import com.timenoteco.timenote.repository.CreationTimenoteData

class CreationTimenoteViewModel: ViewModel() {

    private val timenoteLiveData = MutableLiveData<Timenote>()
    private val createTimenoteData: CreationTimenoteData = CreationTimenoteData()

    /*fun getCreateTimeNoteLiveData(): LiveData<CreationTimenoteData>{
        timenoteLiveData.postValue(createTimenoteData.loadCreateTimenoteData())
        return timenoteLiveData
    }*/

    fun setDescription(description : String){
        timenoteLiveData.value = CreationTimenoteData().setDescription(description)
    }

    fun getDescription(): String?{
        return timenoteLiveData.value?.title
    }

}