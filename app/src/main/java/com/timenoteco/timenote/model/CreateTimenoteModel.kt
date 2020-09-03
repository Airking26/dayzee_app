package com.timenoteco.timenote.model

import android.graphics.Bitmap

enum class StatusTimenote{
    FREE, PAID, NOANSWER
}

data class CreateTimenoteModel (
    var pic: MutableList<AWSFile>?,
    var place: String?,
    var title: String?,
    var desc: String?,
    var year: String?,
    var startDate: String?,
    var endDate: String?,
    var category: String?,
    var color: String?,
    var formatedStartDate: String?,
    var formatedEndDate: String?,
    var price: Long?,
    var url: String?,
    var status: StatusTimenote?
)