package com.timenoteco.timenote.adapter

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.timenoteco.timenote.listeners.TimenoteOptionsListener
import com.timenoteco.timenote.model.Json4Kotlin_Base
import com.timenoteco.timenote.model.Timenote
import java.sql.Time

class TimenotePagingAdapter(diffCallbacks: DiffUtil.ItemCallback<Json4Kotlin_Base>, private val timenoteListenerListener: TimenoteOptionsListener, val fragment: Fragment) : PagingDataAdapter<Json4Kotlin_Base, ItemTimenoteAdapter.TimenoteViewHolder>(diffCallbacks){

    override fun onBindViewHolder(holder: ItemTimenoteAdapter.TimenoteViewHolder, position: Int) =
        holder.bindTimenote(getItem(position) as Timenote, timenoteListenerListener, fragment)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemTimenoteAdapter.TimenoteViewHolder =
        ItemTimenoteAdapter.TimenoteViewHolder(parent)

}

object TimenoteComparator : DiffUtil.ItemCallback<Json4Kotlin_Base>(){

    override fun areItemsTheSame(oldItem: Json4Kotlin_Base, newItem: Json4Kotlin_Base): Boolean {
        return oldItem.createdBy == newItem.createdBy
    }

    override fun areContentsTheSame(oldItem: Json4Kotlin_Base, newItem: Json4Kotlin_Base): Boolean {
        return oldItem == newItem
    }

}