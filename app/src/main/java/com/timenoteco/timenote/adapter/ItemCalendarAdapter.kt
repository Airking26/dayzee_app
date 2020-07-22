package com.timenoteco.timenote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.timenoteco.timenote.R
import com.timenoteco.timenote.androidView.calendar.data.Day
import com.timenoteco.timenote.model.EventCalendar
import kotlinx.android.synthetic.main.item_profile_calendar.view.*
import java.text.SimpleDateFormat
import java.util.*

class ItemCalendarAdapter(
    private var events: MutableList<EventCalendar>,
    private val allEvents: MutableList<EventCalendar>
): RecyclerView.Adapter<ItemCalendarAdapter.TimenoteListHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimenoteListHolder =
        TimenoteListHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_profile_calendar, parent, false))

    override fun getItemCount(): Int = events.size

    override fun onBindViewHolder(holder: TimenoteListHolder, position: Int) {
        holder.bindListStyleItem(events[position])
    }

    fun checkEventForDay(day: Day) {

        val simpleDateFormatYear = SimpleDateFormat("yyyy", Locale.getDefault())
        val simpleDateFormatMonth = SimpleDateFormat("M", Locale.getDefault())
        val simpleDateFormatDay = SimpleDateFormat("d", Locale.getDefault())

        val eventsOfDay: MutableList<EventCalendar> = mutableListOf()
        for(e in allEvents){
            val dayOfEvent = simpleDateFormatDay.format(e.date)
            val daySelected = day.day.toString()
            val monthOfEvent = simpleDateFormatMonth.format(e.date)
            val monthSelected = (day.month + 1).toString()
            val yearOfEvent = simpleDateFormatYear.format(e.date)
            val yearSelected = day.year.toString()
            if(daySelected == dayOfEvent && monthSelected == monthOfEvent && yearSelected == yearOfEvent){
                eventsOfDay.add(e)
            }
        }
        this.events = eventsOfDay
        notifyDataSetChanged()
    }

    class TimenoteListHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bindListStyleItem(event: EventCalendar) {
            itemView.profile_calendar_item_name_event.text = event.name
            itemView.profile_calendar_item_address_event.text = event.address

            Glide
                .with(itemView)
                .load(event.eventPic)
                .apply(RequestOptions.circleCropTransform())
                .into(itemView.profile_calendar_item_pic_event_imageview)
        }

    }
}