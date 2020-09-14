package com.timenoteco.timenote.listeners

import com.timenoteco.timenote.model.TimenoteInfoDTO

interface TimenoteOptionsListener {
    fun onReportClicked()
    fun onEditClicked()
    fun onAlarmClicked()
    fun onDeleteClicked()
    fun onDuplicateClicked(timenoteInfoDTO: TimenoteInfoDTO)
    fun onAddressClicked()
    fun onSeeMoreClicked()
    fun onCommentClicked()
    fun onPlusClicked()
    fun onPictureClicked()
    fun onHideToOthersClicked()
    fun onMaskThisUser()
    fun onDoubleClick()
    fun onSeeParticipants()
}