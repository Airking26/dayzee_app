package com.dayzeeco.dayzee.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.common.Utils
import com.dayzeeco.dayzee.model.TimenoteInfoDTO
import kotlin.time.ExperimentalTime

class TimenoteRecentPagingAdapter(diffCallback: DiffUtil.ItemCallback<TimenoteInfoDTO>, val timenoteRecentClicked: ItemTimenoteRecentAdapter.TimenoteRecentClicked, val utils: Utils) :
    PagingDataAdapter<TimenoteInfoDTO, ItemTimenoteRecentAdapter.TimenoteToComeViewHolder>(diffCallback){

    @OptIn(ExperimentalTime::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ItemTimenoteRecentAdapter.TimenoteToComeViewHolder, position: Int) {
        holder.bindItem(getItem(position)!!, timenoteRecentClicked, utils)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemTimenoteRecentAdapter.TimenoteToComeViewHolder =
        ItemTimenoteRecentAdapter.TimenoteToComeViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.item_timenote_recent, parent, false))

}