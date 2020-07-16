package com.timenoteco.timenote.model

import android.graphics.Bitmap
import androidx.annotation.Nullable

data class Timenote(
    @Nullable
    var pic_user: String?,
    @Nullable
    var pic: MutableList<String>?,
    @Nullable
    var username: String?,
    @Nullable
    var place: String?,
    @Nullable
    var nbrLikes: String?,
    @Nullable
    var seeComments: String?,
    @Nullable
    var desc: String?,
    @Nullable
    var liked: Boolean?,
    @Nullable
    var year: String?,
    @Nullable
    var month: String?,
    @Nullable
    var date: String?,
    @Nullable
    var title: String?,
    @Nullable
    var dateIn: String?,
    var price: Long?,
    var url: String?,
    var status: statusTimenote)