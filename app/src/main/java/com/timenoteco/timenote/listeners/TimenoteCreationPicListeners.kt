package com.timenoteco.timenote.listeners

import android.net.Uri
import com.timenoteco.timenote.model.AWSFile

interface TimenoteCreationPicListeners {
    fun onCropPicClicked(uri: Uri?)
    fun onAddClicked()
    fun onDeleteClicked(uri: Uri?)
}