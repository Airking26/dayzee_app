package com.timenoteco.timenote.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.timenoteco.timenote.R
import com.timenoteco.timenote.model.TimenoteInfoDTO

class TimenoteRecentPagingAdapter(diffCallback: DiffUtil.ItemCallback<TimenoteInfoDTO>, val timenoteRecentClicked: ItemTimenoteRecentAdapter.TimenoteRecentClicked) :
    PagingDataAdapter<TimenoteInfoDTO, ItemTimenoteRecentAdapter.TimenoteToComeViewHolder>(diffCallback){

    override fun onBindViewHolder(holder: ItemTimenoteRecentAdapter.TimenoteToComeViewHolder, position: Int) {
        holder.bindItem(getItem(position)!!, timenoteRecentClicked)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemTimenoteRecentAdapter.TimenoteToComeViewHolder =
        ItemTimenoteRecentAdapter.TimenoteToComeViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.item_timenote_recent, parent, false))

}