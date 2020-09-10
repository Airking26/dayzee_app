package com.timenoteco.timenote.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.timenoteco.timenote.R
import com.timenoteco.timenote.listeners.ItemProfileCardListener
import com.timenoteco.timenote.listeners.OnRemoveFilterBarListener
import com.timenoteco.timenote.listeners.TimenoteOptionsListener
import com.timenoteco.timenote.model.TimenoteInfoDTO

class ProfileEventPagingAdapter(diffUtilCallback: DiffUtil.ItemCallback<TimenoteInfoDTO>, var showHideFilterBar: Boolean, private val timenoteOptionsListener: TimenoteOptionsListener,
                                private val onRemoveFilterBarListener: OnRemoveFilterBarListener, private val onCardClicked: ItemProfileCardListener)
    : PagingDataAdapter<TimenoteInfoDTO, RecyclerView.ViewHolder>(diffUtilCallback) {

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
            (holder as ItemProfileEventAdapter.TimenoteListHolder).bindListStyleItem(
                getItem(position)!!,
                timenoteOptionsListener,
                onCardClicked
            )
    }


    override fun getItemViewType(position: Int): Int {
        return if(position == 0 && showHideFilterBar) R.layout.fragment_filter else R.layout.item_profile_timenote_list_style
    }

}

object ProfileEventComparator : DiffUtil.ItemCallback<TimenoteInfoDTO>(){

    override fun areItemsTheSame(oldItem: TimenoteInfoDTO, newItem: TimenoteInfoDTO): Boolean {
        return oldItem.createdAt == newItem.createdAt
    }

    override fun areContentsTheSame(oldItem: TimenoteInfoDTO, newItem: TimenoteInfoDTO): Boolean {
        return oldItem == newItem
    }

}