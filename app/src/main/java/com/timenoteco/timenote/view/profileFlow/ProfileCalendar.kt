package com.timenoteco.timenote.view.profileFlow

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.date.dayOfMonth
import com.afollestad.date.month
import com.afollestad.date.year
import com.timenoteco.timenote.R
import com.timenoteco.timenote.adapter.ItemCalendarAdapter
import com.timenoteco.timenote.androidView.calendar.data.CalendarAdapter
import com.timenoteco.timenote.androidView.calendar.data.Day
import com.timenoteco.timenote.androidView.calendar.widget.CollapsibleCalendar
import com.timenoteco.timenote.model.TimenoteDateFilteredDTO
import com.timenoteco.timenote.model.TimenoteInfoDTO
import com.timenoteco.timenote.viewModel.ProfileViewModel
import kotlinx.android.synthetic.main.fragment_profile_calendar.*
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.util.*


class ProfileCalendar: Fragment() {

    private lateinit var calendarAdapter : ItemCalendarAdapter
    private var events: MutableList<TimenoteInfoDTO> = mutableListOf()
    private val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    private val profilViewModel : ProfileViewModel by activityViewModels()
    private lateinit var prefs: SharedPreferences
    val TOKEN: String = "TOKEN"
    private var tokenId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(TOKEN, null)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_profile_calendar, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        calendarAdapter = ItemCalendarAdapter(mutableListOf(), events)

        profile_calendar_rv.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = calendarAdapter
        }
        
        val simpleDateFormatYear = SimpleDateFormat("yyyy", Locale.getDefault())
        val simpleDateFormatMonth = SimpleDateFormat("M", Locale.getDefault())
        val simpleDateFormatDay = SimpleDateFormat("d", Locale.getDefault())

        val today =
            Day(simpleDateFormatYear.format(System.currentTimeMillis()).toInt(),
                simpleDateFormatMonth.format(System.currentTimeMillis()).toInt() - 1,
                simpleDateFormatDay.format(System.currentTimeMillis()).toInt())

        calendarAdapter.checkEventForDay(today)

        val day = calendarView.selectedDay
        val nbrDays = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            YearMonth.of(day?.year!!, day.month).lengthOfMonth()
        } else {
            GregorianCalendar(day?.year!!, day.month, day.day).getActualMaximum(Calendar.DAY_OF_MONTH)
        }


        val startDate = SimpleDateFormat(ISO).format(GregorianCalendar(day.year, day.month - 1, 1).timeInMillis)
        val endDate = SimpleDateFormat(ISO).format(GregorianCalendar(day.year, day.month - 1, nbrDays).timeInMillis)

        profilViewModel.getTimenotesByDate(tokenId!!, TimenoteDateFilteredDTO(startDate, endDate)).observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it.isSuccessful)
                events.addAll(it.body() as List<TimenoteInfoDTO>)
        })

        /*for (e in events){ calendarView.addEventTag(simpleDateFormatYear.format(e.startingAt).toInt(), simpleDateFormatMonth.format(e.startingAt).toInt() - 1, simpleDateFormatDay.format(e.startingAt).toInt(), R.color.colorYellow) }


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
                //for (e in eventsCalendar){ calendarView.addEventTag(simpleDateFormatYear.format(e.date).toInt(), simpleDateFormatMonth.format(e.date).toInt() - 1, simpleDateFormatDay.format(e.date).toInt(), R.color.colorYellow) }
            }

            override fun onDaySelect(day: Day) {
                calendarAdapter.checkEventForDay(day)
            }

            override fun onItemClick(v: View, day: Day) {
            }

            override fun onMonthChange() {}
            override fun onWeekChange(position: Int) {}

        })*/

    }
}