package com.timenoteco.timenote.listeners

import com.timenoteco.timenote.model.AWSFile

interface TimenoteCreationPicListeners {
    fun onCropPicClicked(awsFile: AWSFile?)
    fun onAddClicked()
    fun onDeleteClicked(position: AWSFile?)
}