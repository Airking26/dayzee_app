package com.dayzeeco.dayzee.listeners

import com.dayzeeco.dayzee.model.CalendarEvent
import java.util.ArrayList
import java.util.HashMap

interface SynchronizeWithGoogleCalendarListener {
    fun onSynchronize(mEventsList: ArrayList<CalendarEvent>)
}