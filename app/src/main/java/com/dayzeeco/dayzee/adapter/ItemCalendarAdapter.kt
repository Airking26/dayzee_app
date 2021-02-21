package com.dayzeeco.dayzee.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.androidView.calendar.data.Day
import com.dayzeeco.dayzee.model.TimenoteInfoDTO
import kotlinx.android.synthetic.main.item_profile_calendar.view.*
import java.text.SimpleDateFormat
import java.util.*

class ItemCalendarAdapter(
    private var events: MutableList<TimenoteInfoDTO>,
    private val allEvents: MutableList<TimenoteInfoDTO>,
    private val calendarEventClicked: CalendarEventClicked
): RecyclerView.Adapter<ItemCalendarAdapter.TimenoteListHolder>() {

    interface CalendarEventClicked{
        fun onEventClicked(timenoteInfoDTO: TimenoteInfoDTO)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimenoteListHolder =
        TimenoteListHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_profile_calendar, parent, false))

    override fun getItemCount(): Int = events.size

    override fun onBindViewHolder(holder: TimenoteListHolder, position: Int) {
        holder.bindListStyleItem(events[position], calendarEventClicked)
    }

    fun checkEventForDay(day: Day) {

        val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        val simpleDateFormatYear = SimpleDateFormat("yyyy", Locale.getDefault())
        val simpleDateFormatMonth = SimpleDateFormat("M", Locale.getDefault())
        val simpleDateFormatDay = SimpleDateFormat("d", Locale.getDefault())

        val eventsOfDay: MutableList<TimenoteInfoDTO> = mutableListOf()
        for(e in allEvents){
            val dayOfEvent = simpleDateFormatDay.format(SimpleDateFormat(ISO).parse(e.startingAt))
            val daySelected = day.day.toString()
            val monthOfEvent = simpleDateFormatMonth.format(SimpleDateFormat(ISO).parse(e.startingAt))
            val monthSelected = (day.month + 1).toString()
            val yearOfEvent = simpleDateFormatYear.format(SimpleDateFormat(ISO).parse(e.startingAt))
            val yearSelected = day.year.toString()
            if(daySelected == dayOfEvent && monthSelected == monthOfEvent && yearSelected == yearOfEvent){ eventsOfDay.add(e) }
        }
        this.events = eventsOfDay
        notifyDataSetChanged()
    }

    class TimenoteListHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bindListStyleItem(
            event: TimenoteInfoDTO,
            calendarEventClicked: CalendarEventClicked
        ) {
            itemView.profile_calendar_item_name_event.text = event.title
            itemView.profile_calendar_item_address_event.text = event.location?.address?.address?.plus(", ")?.plus(event.location.address.city)?.plus(" ")?.plus(event.location.address.country)

            itemView.profile_calendar_item_pic_event_imageview.setBackgroundColor(Color.parseColor("#FFFFFF"))
            itemView.profile_calendar_item_pic_event_imageview.setImageDrawable(null)

            if(event.pictures.isNullOrEmpty()) {
                if (!event.colorHex.isNullOrBlank() && !event.colorHex.isNullOrEmpty()) itemView.profile_calendar_item_pic_event_imageview.setBackgroundColor(
                    Color.parseColor(if (event.colorHex?.contains("#")!!) event.colorHex else "#${event.colorHex}")
                )
            }
            else Glide
                .with(itemView)
                .load(event.pictures[0])
                .apply(RequestOptions.circleCropTransform())
                .into(itemView.profile_calendar_item_pic_event_imageview)

            itemView.setOnClickListener { calendarEventClicked.onEventClicked(event) }
        }

    }
}