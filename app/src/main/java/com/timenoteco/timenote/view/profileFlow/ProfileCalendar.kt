package com.timenoteco.timenote.view.profileFlow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.ItemCalendarAdapter
import com.timenoteco.timenote.model.EventCalendar
import kotlinx.android.synthetic.main.fragment_profile_calendar.*
import java.util.*

private lateinit var calendarAdapter : ItemCalendarAdapter
private var eventsCalendar: MutableList<EventCalendar> = mutableListOf()

class ProfileCalendar: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_profile_calendar, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        eventsCalendar = mutableListOf(
            EventCalendar("Beach Party",
                "34 Olhio Street",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg"),            EventCalendar("Beach Party",
                "34 Olhio Street",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg"),            EventCalendar("Beach Party",
                "34 Olhio Street",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg"),            EventCalendar("Beach Party",
                "34 Olhio Street",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg"),            EventCalendar("Beach Party",
                "34 Olhio Street",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg"),            EventCalendar("Beach Party",
                "34 Olhio Street",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg")
        )

        calendarAdapter = ItemCalendarAdapter(
            eventsCalendar
        )

        profile_calendar_rv.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = calendarAdapter
        }

        calendarView.addEventTag(2020, 8, 5, R.color.colorAccent)
        calendarView.setCalendarListener(object: CollapsibleCalendar.CalendarListener{
            override fun onClickListener() {
            }

            override fun onDataUpdate() {
            }

            override fun onDayChanged() {
            }

            override fun onDaySelect() {
            }

            override fun onItemClick(v: View) {
            }

            override fun onMonthChange() {
            }

            override fun onWeekChange(position: Int) {
            }

        })

    }
}