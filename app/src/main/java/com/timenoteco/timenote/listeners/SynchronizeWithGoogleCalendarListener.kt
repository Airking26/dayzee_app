package com.timenoteco.timenote.listeners

import com.timenoteco.timenote.model.CalendarEvent
import java.util.ArrayList
import java.util.HashMap

interface SynchronizeWithGoogleCalendarListener {
    fun onSynchronize(mEventsList: ArrayList<CalendarEvent>)
}