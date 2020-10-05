package com.timenoteco.timenote.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.ViewTarget
import com.timenoteco.timenote.R
import com.timenoteco.timenote.androidView.calendar.data.Day
import com.timenoteco.timenote.model.EventCalendar
import com.timenoteco.timenote.model.TimenoteInfoDTO
import kotlinx.android.synthetic.main.item_profile_calendar.view.*
import java.text.SimpleDateFormat
import java.util.*

class ItemCalendarAdapter(
    private var events: MutableList<TimenoteInfoDTO>,
    private val allEvents: MutableList<TimenoteInfoDTO>
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

        val eventsOfDay: MutableList<TimenoteInfoDTO> = mutableListOf()
        for(e in allEvents){
            val dayOfEvent = simpleDateFormatDay.format(e.startingAt)
            val daySelected = day.day.toString()
            val monthOfEvent = simpleDateFormatMonth.format(e.startingAt)
            val monthSelected = (day.month + 1).toString()
            val yearOfEvent = simpleDateFormatYear.format(e.startingAt)
            val yearSelected = day.year.toString()
            if(daySelected == dayOfEvent && monthSelected == monthOfEvent && yearSelected == yearOfEvent){ eventsOfDay.add(e) }
        }
        this.events = eventsOfDay
        notifyDataSetChanged()
    }

    class TimenoteListHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bindListStyleItem(event: TimenoteInfoDTO) {
            itemView.profile_calendar_item_name_event.text = event.title
            //itemView.profile_calendar_item_address_event.text = event.

            Glide
                .with(itemView)
                .load(event.pictures?.get(0))
                .apply(RequestOptions.circleCropTransform())
                .into(itemView.profile_calendar_item_pic_event_imageview)
        }

    }
}