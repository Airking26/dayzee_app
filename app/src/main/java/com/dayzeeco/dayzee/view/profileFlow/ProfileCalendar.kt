package com.dayzeeco.dayzee.view.profileFlow

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.date.dayOfMonth
import com.afollestad.date.month
import com.afollestad.date.year
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.adapter.ItemCalendarAdapter
import com.dayzeeco.dayzee.androidView.calendar.data.CalendarAdapter
import com.dayzeeco.dayzee.androidView.calendar.data.Day
import com.dayzeeco.dayzee.androidView.calendar.widget.CollapsibleCalendar
import com.dayzeeco.dayzee.common.Utils
import com.dayzeeco.dayzee.common.accessToken
import com.dayzeeco.dayzee.model.TimenoteDateFilteredDTO
import com.dayzeeco.dayzee.model.TimenoteInfoDTO
import com.dayzeeco.dayzee.viewModel.LoginViewModel
import com.dayzeeco.dayzee.viewModel.ProfileViewModel
import kotlinx.android.synthetic.main.fragment_profile_calendar.*
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.util.*


class ProfileCalendar: Fragment(), ItemCalendarAdapter.CalendarEventClicked {

    private lateinit var simpleDateFormatDay: SimpleDateFormat
    private lateinit var simpleDateFormatMonth: SimpleDateFormat
    private lateinit var simpleDateFormatYear: SimpleDateFormat
    private lateinit var calendarAdapter : ItemCalendarAdapter
    private var events: MutableList<TimenoteInfoDTO> = mutableListOf()
    private val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    private val profilViewModel : ProfileViewModel by activityViewModels()
    private val loginViewModel : LoginViewModel by activityViewModels()
    private val args: ProfileCalendarArgs by navArgs()
    private lateinit var prefs: SharedPreferences
    private var tokenId: String? = null
    private val utils = Utils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        tokenId = prefs.getString(accessToken, null)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_profile_calendar, container, false)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        simpleDateFormatYear = SimpleDateFormat("yyyy", Locale.getDefault())
        simpleDateFormatMonth = SimpleDateFormat("M", Locale.getDefault())
        simpleDateFormatDay = SimpleDateFormat("d", Locale.getDefault())

        calendarAdapter = ItemCalendarAdapter(mutableListOf(), events, this)

        val today =
            Day(simpleDateFormatYear.format(System.currentTimeMillis()).toInt(),
                simpleDateFormatMonth.format(System.currentTimeMillis()).toInt() - 1,
                simpleDateFormatDay.format(System.currentTimeMillis()).toInt())


        val day = calendarView.selectedDay
        val nbrDays = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) YearMonth.of(day?.year!!, day.month).lengthOfMonth()
         else GregorianCalendar(day?.year!!, day.month, day.day).getActualMaximum(Calendar.DAY_OF_MONTH)

        val startDate = SimpleDateFormat(ISO).format(GregorianCalendar(day.year, day.month - 1, 1).timeInMillis)
        val endDate = SimpleDateFormat(ISO).format(GregorianCalendar(day.year, day.month - 1, nbrDays).timeInMillis)

        loadData(startDate, endDate, today)

        calendarView.setCalendarListener(object : CollapsibleCalendar.CalendarListener {
            override fun onClickListener() {}
            override fun onDataUpdate() {}
            override fun onDayChanged() {}

            override fun onDialogDissmissed() {
                utils.hideStatusBar(requireActivity())
            }

            override fun onDatePicked(datetime: Calendar) {
                val monthActual = calendarView.month + 1
                calendarView.setAdapter(CalendarAdapter(requireContext(), datetime))
                val day = Day(datetime.year, datetime.month + 1, datetime.dayOfMonth)
                val selectedMonth = datetime.month + 1
                calendarView.select(day)
                val nbrOfDays =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) YearMonth.of(day.year, day.month).lengthOfMonth()
                    else GregorianCalendar(day.year, day.month, day.day).getActualMaximum(Calendar.DAY_OF_MONTH)

                val startDate = SimpleDateFormat(ISO).format(GregorianCalendar(day.year, day.month - 1, 1).timeInMillis)
                val endDate = SimpleDateFormat(ISO).format(GregorianCalendar(day.year, day.month - 1, nbrOfDays).timeInMillis)
                if (monthActual != selectedMonth) loadData(startDate, endDate, day)
            }

            override fun onDaySelect(day: Day) {
                calendarAdapter.checkEventForDay(day)
            }

            override fun onItemClick(v: View, day: Day) {}

            override fun onMonthChange(day: Day?) {
                val nbrOfDays =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) YearMonth.of(day?.year!!, day.month + 1).lengthOfMonth()
                    else GregorianCalendar(day?.year!!, day.month, day.day).getActualMaximum(Calendar.DAY_OF_MONTH)

                val startDate = SimpleDateFormat(ISO).format(GregorianCalendar(day.year, day.month, 1).timeInMillis)
                val endDate = SimpleDateFormat(ISO).format(GregorianCalendar(day.year, day.month, nbrOfDays).timeInMillis)
                loadData(startDate, endDate, day)
            }

            override fun onWeekChange(position: Int) {}

        })

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadData(
        startDate: String,
        endDate: String,
        day: Day
    ) {
        getTimenotesByDate(startDate, endDate, day)
    }

    private fun getTimenotesByDate(
        startDate: String,
        endDate: String,
        day: Day
    ) {
        profilViewModel.getTimenotesByDate(tokenId!!,  TimenoteDateFilteredDTO(startDate, endDate), args.id)
            .observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                if(it.code() == 401) {
                    loginViewModel.refreshToken(prefs).observe(viewLifecycleOwner, androidx.lifecycle.Observer { newAccessToken ->
                        tokenId = newAccessToken
                        getTimenotesByDate(startDate, endDate, day)
                    })
                }
                if (it.isSuccessful) {
                    events.clear()
                    events.addAll(it.body() as List<TimenoteInfoDTO>)
                    calendarAdapter = ItemCalendarAdapter(mutableListOf(), events, this)

                    profile_calendar_rv.apply {
                        layoutManager = LinearLayoutManager(requireContext())
                        adapter = calendarAdapter
                    }

                    calendarAdapter.checkEventForDay(day)

                    for (e in events) {
                        calendarView.addEventTag(
                            utils.setYear(e.startingAt).toInt(),
                            simpleDateFormatMonth.format(SimpleDateFormat(ISO).parse(e.startingAt))
                                .toInt() - 1,
                            simpleDateFormatDay.format(SimpleDateFormat(ISO).parse(e.startingAt))
                                .toInt(),
                            R.color.colorYellow
                        )
                    }
                }


            })
    }

    override fun onEventClicked(timenoteInfoDTO: TimenoteInfoDTO) {
        findNavController().navigate(ProfileCalendarDirections.actionGlobalDetailedTimenote(4, timenoteInfoDTO))
    }
}