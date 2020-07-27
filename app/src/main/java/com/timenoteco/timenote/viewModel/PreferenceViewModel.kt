package com.timenoteco.timenote.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.timenoteco.timenote.model.Preference
import com.timenoteco.timenote.view.loginFlow.PreferenceData

class PreferenceViewModel: ViewModel() {

    private val preference = MutableLiveData<MutableList<Preference>>()

    fun getPreferences() : LiveData<MutableList<Preference>>{
        loadPreferences(PreferenceData().loadPreferences())
        return preference
    }

    fun setStatusCategory(index: Int){
        preference.value = PreferenceData()
            .changeStatusCategory(index)
    }

    fun setLikedLevelSubCategory(likedLevel: Int, categoryName: String, subCategoryPosition: Int){
        preference.value = PreferenceData()
            .setLikedLevelSubCategory(likedLevel, categoryName, subCategoryPosition)
    }

    fun closeChip(name: String){
        preference.value = PreferenceData()
            .closeChip(name)
    }

    private fun loadPreferences(listPreferences: MutableList<Preference>) {
        preference.postValue(listPreferences)
    }

}