package com.dayzeeco.dayzee.listeners

import com.dayzeeco.dayzee.model.TimenoteInfoDTO
import com.dayzeeco.dayzee.model.UserInfoDTO

interface TimenoteOptionsListener {
    fun onReportClicked(timenoteInfoDTO: TimenoteInfoDTO)
    fun onEditClicked(timenoteInfoDTO: TimenoteInfoDTO)
    fun onAlarmClicked(timenoteInfoDTO: TimenoteInfoDTO, type: Int)
    fun onDeleteClicked(timenoteInfoDTO: TimenoteInfoDTO)
    fun onDuplicateClicked(timenoteInfoDTO: TimenoteInfoDTO)
    fun onAddressClicked(timenoteInfoDTO: TimenoteInfoDTO)
    fun onSeeMoreClicked(timenoteInfoDTO: TimenoteInfoDTO)
    fun onCommentClicked(timenoteInfoDTO: TimenoteInfoDTO)
    fun onPlusClicked(timenoteInfoDTO: TimenoteInfoDTO, isAdded: Boolean)
    fun onPictureClicked(userInfoDTO: UserInfoDTO)
    fun onHideToOthersClicked(timenoteInfoDTO: TimenoteInfoDTO)
    fun onMaskThisUser()
    fun onDoubleClick()
    fun onSeeParticipants(timenoteInfoDTO: TimenoteInfoDTO)
    fun onShareClicked(timenoteInfoDTO: TimenoteInfoDTO)
    fun onAddMarker(timenoteInfoDTO: TimenoteInfoDTO)
    fun onHashtagClicked(timenoteInfoDTO: TimenoteInfoDTO, hashtag: String?)
}