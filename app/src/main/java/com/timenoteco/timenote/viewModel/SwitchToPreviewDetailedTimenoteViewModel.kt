package com.timenoteco.timenote.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.timenoteco.timenote.model.TimenoteInfoDTO

class SwitchToPreviewDetailedTimenoteViewModel : ViewModel(){

    private val switchToPreviewDetailedTimenoteViewModel = MutableLiveData<Boolean>()
    private var timenoteInfoDTO: TimenoteInfoDTO? = null

    fun switchToPreviewDetailedTimenoteViewModel(boolean: Boolean): LiveData<Boolean>{
        switchToPreviewDetailedTimenoteViewModel.postValue(boolean)
        return switchToPreviewDetailedTimenoteViewModel
    }

    fun getswitchToPreviewDetailedTimenoteViewModel(): LiveData<Boolean> {
        return switchToPreviewDetailedTimenoteViewModel
    }

    fun setTimenoteInfoDTO(timenoteInfoDTO: TimenoteInfoDTO){
        this.timenoteInfoDTO = timenoteInfoDTO
    }

    fun getTimenoteInfoDTO(): TimenoteInfoDTO? {
        return  this.timenoteInfoDTO
    }
}
