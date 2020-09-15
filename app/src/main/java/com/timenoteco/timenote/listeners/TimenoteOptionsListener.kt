package com.timenoteco.timenote.listeners

import com.timenoteco.timenote.model.TimenoteInfoDTO

interface TimenoteOptionsListener {
    fun onReportClicked()
    fun onEditClicked()
    fun onAlarmClicked(event: TimenoteInfoDTO)
    fun onDeleteClicked()
    fun onDuplicateClicked(timenoteInfoDTO: TimenoteInfoDTO)
    fun onAddressClicked()
    fun onSeeMoreClicked(event: TimenoteInfoDTO)
    fun onCommentClicked(event: TimenoteInfoDTO)
    fun onPlusClicked()
    fun onPictureClicked()
    fun onHideToOthersClicked()
    fun onMaskThisUser()
    fun onDoubleClick()
    fun onSeeParticipants()
}