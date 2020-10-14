package com.timenoteco.timenote.webService

import com.timenoteco.timenote.model.SocialMedias
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.timenoteco.timenote.model.*
import java.lang.reflect.Type

class ProfileModifyData(context: Context) {

    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val type: Type = object : TypeToken<UserInfoDTO?>() {}.type

    private var profilModifyModel: UserInfoDTO? = Gson().fromJson<UserInfoDTO>(
        prefs.getString("UserInfoDTO",
        Gson().toJson(UserInfoDTO(status = 0, dateFormat = 0, socialMedias =
        SocialMedias(
            Youtube("", false),
            Facebook("", false),
            Instagram("", false),
            WhatsApp("", false),
            LinkedIn("", false))
        ))), type)

    fun loadProfileModifyModel(): UserInfoDTO? {
        return profilModifyModel
    }

    fun setNbrFollowers(nbrFollowers: Int){
        profilModifyModel?.followers = nbrFollowers
    }

    fun setNbrFollowing(nbrFollowing: Int){
        profilModifyModel?.following = nbrFollowing
    }

    fun setPicture(url: String){
        profilModifyModel?.picture = url
        notifyProfileDataChanged()
    }

    private fun notifyProfileDataChanged() {
        prefs.edit().putString("UserInfoDTO", Gson().toJson(profilModifyModel)).apply()
    }

    fun setName(name: String){
        profilModifyModel?.givenName = name
        notifyProfileDataChanged()
    }

    fun setLocation(location: Location) {
        profilModifyModel?.location = location
        notifyProfileDataChanged()
    }

    fun setBirthday(birthday: String) {
        profilModifyModel?.birthday = birthday
        notifyProfileDataChanged()
    }

    fun setGender(gender: String) {
        profilModifyModel?.gender = gender
        notifyProfileDataChanged()
    }

    fun setStatusAccount(statusAccount: Int) {
        profilModifyModel?.status = statusAccount
        notifyProfileDataChanged()
    }

    fun setFormatTimenote(formatTimenote: Int) {
        profilModifyModel?.dateFormat = formatTimenote
        notifyProfileDataChanged()
    }

    fun setYoutubeLink(link: String) {
        profilModifyModel?.socialMedias?.youtube?.url = link
        notifyProfileDataChanged()
    }

    fun setFacebookLink(link: String){
        profilModifyModel?.socialMedias?.facebook?.url = link
        notifyProfileDataChanged()
    }

    fun setInstaLink(link: String){
        profilModifyModel?.socialMedias?.instagram?.url = link
        profilModifyModel
    }

    fun setWhatsappLink(link: String){
        profilModifyModel?.socialMedias?.whatsApp?.url = link
        notifyProfileDataChanged()
    }

    fun setLinkedinLink(link: String){
        profilModifyModel?.socialMedias?.linkedIn?.url = link
        notifyProfileDataChanged()
    }

    fun setDescription(description: String) {
        profilModifyModel?.description = description
        notifyProfileDataChanged()
    }

    fun setStateSwitch(state: Int){
        when(state){
            0 -> {
                profilModifyModel?.socialMedias?.youtube?.enabled = true
                profilModifyModel?.socialMedias?.facebook?.enabled = false
                profilModifyModel?.socialMedias?.instagram?.enabled = false
                profilModifyModel?.socialMedias?.whatsApp?.enabled = false
                profilModifyModel?.socialMedias?.linkedIn?.enabled = false
            }
            1 -> {
                profilModifyModel?.socialMedias?.youtube?.enabled = false
                profilModifyModel?.socialMedias?.facebook?.enabled = true
                profilModifyModel?.socialMedias?.instagram?.enabled = false
                profilModifyModel?.socialMedias?.whatsApp?.enabled = false
                profilModifyModel?.socialMedias?.linkedIn?.enabled = false
            }
            2 ->{
                profilModifyModel?.socialMedias?.youtube?.enabled = false
                profilModifyModel?.socialMedias?.facebook?.enabled = false
                profilModifyModel?.socialMedias?.instagram?.enabled = true
                profilModifyModel?.socialMedias?.whatsApp?.enabled = false
                profilModifyModel?.socialMedias?.linkedIn?.enabled = false
            }
            3 -> {
                profilModifyModel?.socialMedias?.youtube?.enabled = false
                profilModifyModel?.socialMedias?.facebook?.enabled = false
                profilModifyModel?.socialMedias?.instagram?.enabled = false
                profilModifyModel?.socialMedias?.whatsApp?.enabled = true
                profilModifyModel?.socialMedias?.linkedIn?.enabled = false
            }
            4 -> {
                profilModifyModel?.socialMedias?.youtube?.enabled = false
                profilModifyModel?.socialMedias?.facebook?.enabled = false
                profilModifyModel?.socialMedias?.instagram?.enabled = false
                profilModifyModel?.socialMedias?.whatsApp?.enabled = false
                profilModifyModel?.socialMedias?.linkedIn?.enabled = true
            }
        }
        notifyProfileDataChanged()
    }
}
