package com.timenoteco.timenote.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.timenoteco.timenote.model.ProfilModifyModel
import com.timenoteco.timenote.repository.ProfileModifyData

class ProfileModifyViewModel: ViewModel() {

    private val profilModifyLive = MutableLiveData<ProfilModifyModel>()
    private val profileModifyData: ProfileModifyData = ProfileModifyData()

    fun getProfileModify(): LiveData<ProfilModifyModel>{
        profilModifyLive.postValue(profileModifyData.loadProfileModifyModel())
        return profilModifyLive
    }

    fun setNameAppearance(nameApperance: String){
        profilModifyLive.postValue(profileModifyData.setNameAppearance(nameApperance))
    }

    fun setName(name: String){
        profilModifyLive.postValue(profileModifyData.setName(name))
    }

    fun setLocation(location: String){
        profilModifyLive.postValue(profileModifyData.setLocation(location))
    }

    fun setBirthday(birthday: String){
        profilModifyLive.postValue(profileModifyData.setBirthday(birthday))
    }

    fun setGender(gender: Int){
        profilModifyLive.postValue(profileModifyData.setGender(gender))
    }

    fun setStatusAccount(statusAccount: Int){
        profilModifyLive.postValue(profileModifyData.setStatusAccount(statusAccount))
    }

    fun setFormatTimenote(formatTimenote: Int){
        profilModifyLive.postValue(profileModifyData.setFormatTimenote(formatTimenote))
    }

    fun setLink(link: String){
        profilModifyLive.postValue(profileModifyData.setLink(link))
    }

    fun setDescription(description: String){
        profilModifyLive.postValue(profileModifyData.setDescription(description))
    }
}