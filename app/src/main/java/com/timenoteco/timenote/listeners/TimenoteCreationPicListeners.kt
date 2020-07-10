package com.timenoteco.timenote.listeners

import android.graphics.Bitmap
import android.net.Uri

interface TimenoteCreationPicListeners {
    fun onChangePicClicked(position: Int)
    fun onCropPicClicked(bitmap: Uri, position: Int)
    fun onAddClicked()
    fun onDeleteClicked(position: Int)
}