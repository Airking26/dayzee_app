package com.timenoteco.timenote.listeners

import com.timenoteco.timenote.model.UserInfoDTO

interface RefreshPicBottomNavListener {

    fun onrefreshPicBottomNav(userInfoDTO: UserInfoDTO?)
}