package com.timenoteco.timenote.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.timenoteco.timenote.R
import com.timenoteco.timenote.listeners.OnRemoveFilterBarListener
import com.timenoteco.timenote.listeners.TimenoteOptionsListener
import com.timenoteco.timenote.model.Timenote
import com.timenoteco.timenote.model.TimenoteModel

class ProfileEventPagingAdapter(diffUtilCallback: DiffUtil.ItemCallback<TimenoteModel>,
                                var showHideFilterBar: Boolean, private val timenoteOptionsListener: TimenoteOptionsListener,
                                private val onRemoveFilterBarListener: OnRemoveFilterBarListener)
    : PagingDataAdapter<TimenoteModel, RecyclerView.ViewHolder>(diffUtilCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == R.layout.item_profile_timenote_list_style)
            ItemProfileEventAdapter.TimenoteListHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_profile_timenote_list_style, parent, false))
        else ItemProfileEventAdapter.TimenoteListHolder(LayoutInflater.from(parent.context).inflate(R.layout.fragment_filter, parent, false))

    }

    fun showHideFilterBar(boolean: Boolean){
        this.showHideFilterBar = boolean
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(position == 0 && showHideFilterBar) (holder as ItemProfileEventAdapter.TimenoteListHolder).showFilterBar(onRemoveFilterBarListener) else
            (holder as ItemProfileEventAdapter.TimenoteListHolder).bindListStyleItem(getItem(position) as Timenote, timenoteOptionsListener)
    }


    override fun getItemViewType(position: Int): Int {
        return if(position == 0 && showHideFilterBar) R.layout.fragment_filter else R.layout.item_profile_timenote_list_style
    }

}

object ProfileEventComparator : DiffUtil.ItemCallback<TimenoteModel>(){

    override fun areItemsTheSame(oldItem: TimenoteModel, newItem: TimenoteModel): Boolean {
        return oldItem.createdAt == newItem.createdAt
    }

    override fun areContentsTheSame(oldItem: TimenoteModel, newItem: TimenoteModel): Boolean {
        return oldItem == newItem
    }

}