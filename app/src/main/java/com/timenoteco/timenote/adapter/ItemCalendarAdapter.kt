package com.timenoteco.timenote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.timenoteco.timenote.R
import com.timenoteco.timenote.model.Event
import com.timenoteco.timenote.model.EventCalendar
import kotlinx.android.synthetic.main.item_profile_calendar.view.*
import kotlinx.android.synthetic.main.item_profile_timenote_list_style.view.*

class ItemCalendarAdapter(private var events: List<EventCalendar>): RecyclerView.Adapter<ItemCalendarAdapter.TimenoteListHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimenoteListHolder =
        TimenoteListHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_profile_calendar, parent, false))

    override fun getItemCount(): Int = events.size

    override fun onBindViewHolder(holder: TimenoteListHolder, position: Int) {
        holder.bindListStyleItem(events[position])
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