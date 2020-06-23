package com.timenoteco.timenote.model

import android.graphics.Bitmap
import androidx.annotation.Nullable

data class CreateTimenote (
    @Nullable
    var pic: Bitmap?,
    @Nullable
    var place: String?,
    @Nullable
    var desc: String?,
    @Nullable
    var year: String?,
    @Nullable
    var month: String?,
    @Nullable
    var date: String?,
    @Nullable
    var title: String?,
    @Nullable
    var dateIn: String?
)