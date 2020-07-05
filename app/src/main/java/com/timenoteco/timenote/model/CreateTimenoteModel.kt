package com.timenoteco.timenote.model

import android.graphics.Bitmap
import androidx.annotation.Nullable

data class CreateTimenoteModel (
    @Nullable
    var pic: Bitmap?,
    @Nullable
    var place: String?,
    @Nullable
    var title: String?,
    @Nullable
    var desc: String?,
    @Nullable
    var year: String?,
    @Nullable
    var startDate: String?,
    @Nullable
    var endDate: String?,
    @Nullable
    var category: String?,
    @Nullable
    var color: String?,
    var formatedStartDate: String?,
    var formatedEndDate: String?
)