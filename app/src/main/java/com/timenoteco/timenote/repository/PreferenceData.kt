package com.timenoteco.timenote.repository

import com.timenoteco.timenote.model.Category
import com.timenoteco.timenote.model.Preference
import com.timenoteco.timenote.model.SubCategory


private var preferences = mutableListOf(
    Preference(
        Category("Religion", false), listOf(
            SubCategory("Judaisme", 0),
            SubCategory("Bouddhisme", 2),
            SubCategory("Islam", 1)
        ), 0
    ),
    Preference(
        Category("Sport", false), listOf(
            SubCategory("Football", 0),
            SubCategory("Tennis", 2)
        ), 1
    ),
    Preference(
        Category("Music", false), listOf(
            SubCategory("Electro", 0),
            SubCategory("Techno", 2),
            SubCategory("Classique", 3),
            SubCategory("Hip-Hop", 2)
        ), 2
    )
)

class PreferenceData {

    fun loadPreferences(): MutableList<Preference>{
        return preferences
    }

    fun changeStatusCategory(index: Int): MutableList<Preference>{
        preferences[index].category.isSelected = !preferences[index].category.isSelected
        return preferences
    }

    fun closeChip(name: String): MutableList<Preference>{
        preferences.forEach { if(it.category.name == name) it.category.isSelected = !it.category.isSelected  }
        return preferences
    }

    fun setLikedLevelSubCategory(likedLevel: Int, categoryName: String, subCategoryPosition: Int): MutableList<Preference>{
        preferences.forEach{if (it.category.name == categoryName) it.subCategories[subCategoryPosition].appreciated = likedLevel}
        return preferences
    }
}