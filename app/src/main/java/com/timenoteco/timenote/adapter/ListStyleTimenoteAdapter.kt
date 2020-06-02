package com.timenoteco.timenote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.timenoteco.timenote.R
import com.timenoteco.timenote.model.Event
import kotlinx.android.synthetic.main.item_profile_timenote_list_style.view.*
import kotlinx.android.synthetic.main.item_suggestion.view.*

class ListStyleTimenoteAdapter(private var events: List<Event>): RecyclerView.Adapter<ListStyleTimenoteAdapter.TimenoteListHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimenoteListHolder =
        TimenoteListHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_profile_timenote_list_style, parent, false))

    override fun getItemCount(): Int = events.size

    override fun onBindViewHolder(holder: TimenoteListHolder, position: Int) {
        holder.bindListStyleItem(events[position])
    }

    class TimenoteListHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bindListStyleItem(event: Event) {
            itemView.profile_item_name_event.text = event.name
            itemView.profile_item_address_event.text = event.address
            itemView.profile_item_date_event.text = event.date
            Glide
                .with(itemView)
                .load(event.eventPic)
                .into(itemView.profile_item_pic_event_imageview)

            Glide
                .with(itemView)
                .load(event.profilePic)
                .apply(RequestOptions.circleCropTransform())
                .into(itemView.profile_item_pic_profile_imageview)
        }

    }
}