package com.timenoteco.timenote.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProfilModifyModel(
    var nameAppearance: String?,
    var name: String?,
    var location: String?,
    var birthday: String?,
    var gender: Int?,
    var statusAccount: Int?,
    var formatTimenote: Int?,
    var link: String?,
    var description: String?) : Parcelable