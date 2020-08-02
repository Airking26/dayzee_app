package com.timenoteco.timenote.view.loginFlow

import com.timenoteco.timenote.model.Category
import com.timenoteco.timenote.model.Preference
import com.timenoteco.timenote.model.SubCategory


private var preferences : MutableList<Preference> = mutableListOf()

class PreferenceData {

    fun loadPreferences(): MutableList<Preference>{
        return preferences
    }

    fun changeStatusCategory(index: Int): MutableList<Preference>{
      //  preferences[index].category.isSelected = !preferences[index].category.isSelected
        return preferences
    }

    fun closeChip(name: String): MutableList<Preference>{
        //preferences.forEach { if(it.category.name == name) it.category.isSelected = !it.category.isSelected  }
        return preferences
    }

    fun setLikedLevelSubCategory(likedLevel: Int, categoryName: String, subCategoryPosition: Int): MutableList<Preference>{
        //preferences.forEach{if (it.category.name == categoryName) it.subCategories[subCategoryPosition].appreciated = likedLevel}
        return preferences
    }
}