package com.timenoteco.timenote.adapter

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.timenoteco.timenote.listeners.TimenoteOptionsListener
import com.timenoteco.timenote.model.TimenoteInfoDTO

class TimenotePagingAdapter(diffCallbacks: DiffUtil.ItemCallback<TimenoteInfoDTO>,
                            private val timenoteListenerListener: TimenoteOptionsListener,
                            val fragment: Fragment, private val isFromFuture: Boolean)
    : PagingDataAdapter<TimenoteInfoDTO, ItemTimenoteAdapter.TimenoteViewHolder>(diffCallbacks){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemTimenoteAdapter.TimenoteViewHolder =
        ItemTimenoteAdapter.TimenoteViewHolder(parent)

    override fun onBindViewHolder(holder: ItemTimenoteAdapter.TimenoteViewHolder, position: Int) =
        holder.bindTimenote(
            getItem(position) as TimenoteInfoDTO,
            timenoteListenerListener,
            fragment,
            isFromFuture
        )

}

object TimenoteComparator : DiffUtil.ItemCallback<TimenoteInfoDTO>(){

    override fun areItemsTheSame(oldItem: TimenoteInfoDTO, newItem: TimenoteInfoDTO): Boolean {
        return oldItem.createdBy == newItem.createdBy
    }

    override fun areContentsTheSame(oldItem: TimenoteInfoDTO, newItem: TimenoteInfoDTO): Boolean {
        return oldItem == newItem
    }

}