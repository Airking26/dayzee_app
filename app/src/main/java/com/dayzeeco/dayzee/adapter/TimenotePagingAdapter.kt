package com.dayzeeco.dayzee.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.common.Utils
import com.dayzeeco.dayzee.listeners.TimenoteOptionsListener
import com.dayzeeco.dayzee.model.TimenoteInfoDTO
import kotlin.time.ExperimentalTime

class TimenotePagingAdapter(diffCallbacks: DiffUtil.ItemCallback<TimenoteInfoDTO>,
                            private val timenoteListenerListener: TimenoteOptionsListener,
                            val fragment: Fragment, private val isFromFuture: Boolean,
                            private val utils: Utils, private val createdBy: String?
)
    : PagingDataAdapter<TimenoteInfoDTO, ItemTimenoteAdapter.TimenoteViewHolder>(diffCallbacks){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemTimenoteAdapter.TimenoteViewHolder =
        ItemTimenoteAdapter.TimenoteViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_timenote, parent, false))

    @ExperimentalTime
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ItemTimenoteAdapter.TimenoteViewHolder, position: Int) =
        holder.bindTimenote(
            getItem(position)!!,
            timenoteListenerListener,
            fragment,
            isFromFuture,
            utils, createdBy
        )

}

object TimenoteComparator : DiffUtil.ItemCallback<TimenoteInfoDTO>(){

    override fun areItemsTheSame(oldItem: TimenoteInfoDTO, newItem: TimenoteInfoDTO): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TimenoteInfoDTO, newItem: TimenoteInfoDTO): Boolean {
        return oldItem == newItem
    }

}


object TimenoteComparatorTest : DiffUtil.ItemCallback<TimenoteInfoDTO>(){

    override fun areItemsTheSame(oldItem: TimenoteInfoDTO, newItem: TimenoteInfoDTO): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TimenoteInfoDTO, newItem: TimenoteInfoDTO): Boolean {
        return oldItem == newItem
    }

}