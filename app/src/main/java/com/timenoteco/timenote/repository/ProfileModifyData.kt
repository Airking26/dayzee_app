package com.timenoteco.timenote.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.timenoteco.timenote.model.ProfilModifyModel
import java.lang.reflect.Type

class ProfileModifyData(context: Context) {

    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val type: Type = object : TypeToken<ProfilModifyModel?>() {}.type

    private var profilModifyModel: ProfilModifyModel? = Gson().fromJson<ProfilModifyModel>(
        prefs.getString("profile",
        Gson().toJson(ProfilModifyModel(null, null, null,
            null, null, null, null, null, null))), type)

    fun loadProfileModifyModel(): ProfilModifyModel? {
        return profilModifyModel
    }

    fun setNameAppearance(nameAppearance: String) {
        profilModifyModel?.nameAppearance = nameAppearance
        notifyProfileDataChanged()
    }

    private fun notifyProfileDataChanged() {
        prefs.edit().putString("profile", Gson().toJson(profilModifyModel)).apply()
    }

    fun setName(name: String){
        profilModifyModel?.name = name
        notifyProfileDataChanged()
    }

    fun setLocation(location: String) {
        profilModifyModel?.location = location
        notifyProfileDataChanged()
    }

    fun setBirthday(birthday: String) {
        profilModifyModel?.birthday = birthday
        notifyProfileDataChanged()
    }

    fun setGender(gender: Int) {
        profilModifyModel?.gender = gender
        notifyProfileDataChanged()
    }

    fun setStatusAccount(statusAccount: Int) {
        profilModifyModel?.statusAccount = statusAccount
        notifyProfileDataChanged()
    }

    fun setFormatTimenote(formatTimenote: Int) {
        profilModifyModel?.formatTimenote = formatTimenote
        notifyProfileDataChanged()
    }

    fun setLink(link: String) {
        profilModifyModel?.link = link
        notifyProfileDataChanged()
    }

    fun setDescription(description: String) {
        profilModifyModel?.description = description
        notifyProfileDataChanged()
    }

}
