package com.timenoteco.timenote.adapter

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.timenoteco.timenote.listeners.TimenoteOptionsListener
import com.timenoteco.timenote.model.Timenote
import com.timenoteco.timenote.model.TimenoteModel

class TimenotePagingAdapter(diffCallbacks: DiffUtil.ItemCallback<TimenoteModel>,
                            private val timenoteListenerListener: TimenoteOptionsListener,
                            val fragment: Fragment)
    : PagingDataAdapter<TimenoteModel, ItemTimenoteAdapter.TimenoteViewHolder>(diffCallbacks){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemTimenoteAdapter.TimenoteViewHolder =
        ItemTimenoteAdapter.TimenoteViewHolder(parent)

    override fun onBindViewHolder(holder: ItemTimenoteAdapter.TimenoteViewHolder, position: Int) =
        holder.bindTimenote(getItem(position) as Timenote, timenoteListenerListener, fragment)

}

object TimenoteComparator : DiffUtil.ItemCallback<TimenoteModel>(){

    override fun areItemsTheSame(oldItem: TimenoteModel, newItem: TimenoteModel): Boolean {
        return oldItem.createdBy == newItem.createdBy
    }

    override fun areContentsTheSame(oldItem: TimenoteModel, newItem: TimenoteModel): Boolean {
        return oldItem == newItem
    }

}