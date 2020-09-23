package com.timenoteco.timenote.listeners

import com.timenoteco.timenote.model.TimenoteInfoDTO

interface TimenoteOptionsListener {
    fun onReportClicked()
    fun onEditClicked(timenoteInfoDTO: TimenoteInfoDTO)
    fun onAlarmClicked(timenoteInfoDTO: TimenoteInfoDTO)
    fun onDeleteClicked(timenoteInfoDTO: TimenoteInfoDTO)
    fun onDuplicateClicked(timenoteInfoDTO: TimenoteInfoDTO)
    fun onAddressClicked(timenoteInfoDTO: TimenoteInfoDTO)
    fun onSeeMoreClicked(timenoteInfoDTO: TimenoteInfoDTO)
    fun onCommentClicked(timenoteInfoDTO: TimenoteInfoDTO)
    fun onPlusClicked(timenoteInfoDTO: TimenoteInfoDTO)
    fun onPictureClicked(timenoteInfoDTO: TimenoteInfoDTO)
    fun onHideToOthersClicked(timenoteInfoDTO: TimenoteInfoDTO)
    fun onMaskThisUser()
    fun onDoubleClick()
    fun onSeeParticipants(timenoteInfoDTO: TimenoteInfoDTO)
    fun onShareClicked(timenoteInfoDTO: TimenoteInfoDTO)
}