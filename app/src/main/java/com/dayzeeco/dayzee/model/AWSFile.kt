package com.dayzeeco.dayzee.model

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AWSFile(
    var uri: Uri? = null,
    var bitmap: Bitmap? = null
) : Parcelable