package com.timenoteco.timenote.listeners

import com.timenoteco.timenote.model.TimenoteInfoDTO

interface ItemProfileCardListener {
    fun onCardClicked(event: TimenoteInfoDTO)
}