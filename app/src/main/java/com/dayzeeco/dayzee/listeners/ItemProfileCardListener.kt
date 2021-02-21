package com.dayzeeco.dayzee.listeners

import com.dayzeeco.dayzee.model.TimenoteInfoDTO

interface ItemProfileCardListener {
    fun onCardClicked(event: TimenoteInfoDTO)
}