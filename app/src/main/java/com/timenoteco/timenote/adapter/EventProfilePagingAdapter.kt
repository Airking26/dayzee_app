package com.timenoteco.timenote.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.list.listItems
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.timenoteco.timenote.R
import com.timenoteco.timenote.common.Utils
import com.timenoteco.timenote.listeners.ItemProfileCardListener
import com.timenoteco.timenote.listeners.OnRemoveFilterBarListener
import com.timenoteco.timenote.listeners.TimenoteOptionsListener
import com.timenoteco.timenote.model.TimenoteInfoDTO
import kotlinx.android.synthetic.main.fragment_filter.view.*
import kotlinx.android.synthetic.main.item_profile_timenote_list_style.view.*

class EventProfilePagingAdapter (diffCallback: DiffUtil.ItemCallback<TimenoteInfoDTO>)
    : PagingDataAdapter<TimenoteInfoDTO, EventProfilePagingAdapter.TimenoteListHolder>(diffCallback){

    class TimenoteListHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindListStyleItem(
            event: TimenoteInfoDTO
        ) {
            itemView.profile_item_name_event.text = event.title
            itemView.profile_item_date_event.text = Utils().calculateDecountTime()
            Glide
                .with(itemView)
                .load(event.pictures[0])
                .placeholder(R.drawable.circle_pic)
                .into(itemView.profile_item_pic_event_imageview)

            Glide
                .with(itemView)
                .load(event.createdBy.pictureURL)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.circle_pic)
                .into(itemView.profile_item_pic_profile_imageview)
        }

    }

    override fun onBindViewHolder(holder: TimenoteListHolder, position: Int) {
        holder.bindListStyleItem(getItem(position)!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimenoteListHolder =
        TimenoteListHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_profile_timenote_list_style, parent, false))


}