package com.dayzeeco.dayzee.listeners

import android.net.Uri
import com.dayzeeco.dayzee.model.AWSFile

interface TimenoteCreationPicListeners {
    fun onCropPicClicked(uri: Uri?)
    fun onAddClicked()
    fun onDeleteClicked(uri: Uri?)
}