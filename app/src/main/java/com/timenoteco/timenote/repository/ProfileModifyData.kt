package com.timenoteco.timenote.repository

import com.timenoteco.timenote.model.ProfilModifyModel

class ProfileModifyData {

    private var profilModifyModel:  ProfilModifyModel = ProfilModifyModel(null, null, null, null,
        null, null, null, null, null)

    fun loadProfileModifyModel(): ProfilModifyModel{
        return profilModifyModel
    }

    fun setNameAppearance(nameAppearance: String): ProfilModifyModel{
        profilModifyModel.nameAppearance = nameAppearance
        return profilModifyModel
    }

    fun setName(name: String): ProfilModifyModel{
        profilModifyModel.name = name
        return profilModifyModel
    }

    fun setLocation(location: String): ProfilModifyModel{
        profilModifyModel.location = location
        return profilModifyModel
    }

    fun setBirthday(birthday: String): ProfilModifyModel{
        profilModifyModel.birthday = birthday
        return profilModifyModel
    }

    fun setGender(gender: Int):ProfilModifyModel{
        profilModifyModel.gender = gender
        return profilModifyModel
    }

    fun setStatusAccount(statusAccount: Int): ProfilModifyModel {
        profilModifyModel.statusAccount = statusAccount
        return profilModifyModel
    }

    fun setFormatTimenote(formatTimenote: Int): ProfilModifyModel{
        profilModifyModel.formatTimenote = formatTimenote
        return profilModifyModel
    }

    fun setLink(link: String): ProfilModifyModel{
        profilModifyModel.link = link
        return profilModifyModel
    }

    fun setDescription(description: String): ProfilModifyModel {
        profilModifyModel.description = description
        return profilModifyModel
    }
}
