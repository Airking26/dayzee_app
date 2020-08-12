package com.timenoteco.timenote.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NearbyFilterModel (
    var categories : List<Category>?,
    var from : Int?,
    var paidTimenote: Int?,
    var distance: Int?,
    var whenn : String?,
    var where : Location?
) : Parcelable