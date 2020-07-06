package com.timenoteco.timenote.listeners

import android.graphics.Bitmap

interface TimenoteCreationPicListeners {
    fun onChangePicClicked(position: Int)
    fun onCropPicClicked(bitmap: Bitmap, position: Int)
    fun onAddClicked()
    fun onDeleteClicked(position: Int)
}