package com.timenoteco.timenote.model

import java.util.*

data class CalendarEvent (
    val title: String,
    val begin: Date,
    val end: Date,
    val allDay: Boolean,
    val id: Long,
    val location: String,
    val eventStatus: Int
) : Comparable<CalendarEvent> {
    override fun compareTo(other: CalendarEvent): Int {
        return begin.compareTo(other.begin)
    }
}