package com.timenoteco.timenote.view.profileFlow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.date.dayOfMonth
import com.afollestad.date.month
import com.afollestad.date.year
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.ItemCalendarAdapter
import com.timenoteco.timenote.androidView.calendar.data.CalendarAdapter
import com.timenoteco.timenote.androidView.calendar.data.Day
import com.timenoteco.timenote.androidView.calendar.widget.CollapsibleCalendar
import com.timenoteco.timenote.model.EventCalendar
import kotlinx.android.synthetic.main.fragment_profile_calendar.*
import java.text.SimpleDateFormat
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
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                1595425853001),
            EventCalendar("Beach Party",
                "34 Olhio Street",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                1595429853001),
            EventCalendar("Beach Party",
                "34 Olhio Street",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                1595858713000),
            EventCalendar("Beach Party",
                "34 Olhio Street",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                1595945113000),
            EventCalendar("Beach Party",
                "34 Olhio Street",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                1598623513),
            EventCalendar("Beach Party",
                "34 Olhio Street",
                "https://www.canalvie.com/polopoly_fs/1.9529622.1564082230!/image/plages-pres-quebec.jpg_gen/derivatives/cvlandscape_670_377/plages-pres-quebec.jpg",
                1595425853001)
        )
        calendarAdapter = ItemCalendarAdapter(mutableListOf(), eventsCalendar)

        profile_calendar_rv.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = calendarAdapter
        }


        val simpleDateFormatYear = SimpleDateFormat("yyyy", Locale.getDefault())
        val simpleDateFormatMonth = SimpleDateFormat("M", Locale.getDefault())
        val simpleDateFormatDay = SimpleDateFormat("d", Locale.getDefault())

        val today = Day(simpleDateFormatYear.format(System.currentTimeMillis()).toInt(), simpleDateFormatMonth.format(System.currentTimeMillis()).toInt() - 1, simpleDateFormatDay.format(System.currentTimeMillis()).toInt())

        calendarAdapter.checkEventForDay(today)

        for (e in eventsCalendar){
            calendarView.addEventTag(simpleDateFormatYear.format(e.date).toInt(), simpleDateFormatMonth.format(e.date).toInt() - 1, simpleDateFormatDay.format(e.date).toInt(), R.color.colorAccent)
        }

        calendarView.setCalendarListener(object: CollapsibleCalendar.CalendarListener{
            override fun onClickListener() {}
            override fun onDataUpdate() {}
            override fun onDayChanged() {}

            override fun onDialogDissmissed() {
                com.timenoteco.timenote.common.Utils().hideStatusBar(requireActivity())
            }

            override fun onDatePicked(datetime: Calendar) {
                calendarView.setAdapter(CalendarAdapter(requireContext(), datetime))
                calendarView.select(Day(datetime.year, datetime.month, datetime.dayOfMonth))
                calendarAdapter.checkEventForDay(Day(datetime.year, datetime.month, datetime.dayOfMonth))
                for (e in eventsCalendar){
                    calendarView.addEventTag(simpleDateFormatYear.format(e.date).toInt(), simpleDateFormatMonth.format(e.date).toInt() - 1, simpleDateFormatDay.format(e.date).toInt(), R.color.colorAccent)
                }
            }

            override fun onDaySelect(day: Day) {
                calendarAdapter.checkEventForDay(day)
            }

            override fun onItemClick(v: View, day: Day) {
            }

            override fun onMonthChange() {}
            override fun onWeekChange(position: Int) {}

        })

    }
}