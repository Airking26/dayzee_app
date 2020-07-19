package com.timenoteco.timenote.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.list.listItems
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.timenoteco.timenote.R
import com.timenoteco.timenote.listeners.OnRemoveFilterBarListener
import com.timenoteco.timenote.listeners.TimenoteOptionsListener
import com.timenoteco.timenote.model.Timenote
import kotlinx.android.synthetic.main.fragment_filter.view.*
import kotlinx.android.synthetic.main.item_profile_timenote_list_style.view.*


class ItemProfileEventAdapter(
    private var events: MutableList<Timenote>,
    private val timenoteOptionsListener: TimenoteOptionsListener,
    private val onRemoveFilterBarListener: OnRemoveFilterBarListener,
    private var showHideFilterBar: Boolean
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView: View = if(viewType == R.layout.item_profile_timenote_list_style){
            LayoutInflater.from(parent.context).inflate(R.layout.item_profile_timenote_list_style, parent, false)
        } else {
            LayoutInflater.from(parent.context).inflate(R.layout.fragment_filter, parent, false)
        }
        return TimenoteListHolder(itemView)
    }

    fun showHideFilterBar(boolean: Boolean){
        this.showHideFilterBar = boolean
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = events.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(position == 0 && showHideFilterBar) (holder as TimenoteListHolder).showFilterBar(onRemoveFilterBarListener) else
        (holder as TimenoteListHolder).bindListStyleItem(events[position], timenoteOptionsListener)
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == 0 && showHideFilterBar) R.layout.fragment_filter else R.layout.item_profile_timenote_list_style
    }

    class TimenoteListHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindListStyleItem(event: Timenote, timenoteOptionsListener: TimenoteOptionsListener) {
            itemView.profile_item_name_event.text = event.title
            itemView.profile_item_address_event.text = event.place
            itemView.profile_item_date_event.text = event.dateIn
            itemView.profile_item_options.setOnClickListener { createOptionsOnTimenote(itemView.context, true, timenoteOptionsListener) }
            Glide
                .with(itemView)
                .load(event.pic!![0])
                .into(itemView.profile_item_pic_event_imageview)

            Glide
                .with(itemView)
                .load(event.pic_user)
                .apply(RequestOptions.circleCropTransform())
                .into(itemView.profile_item_pic_profile_imageview)
        }
        private fun createOptionsOnTimenote(context: Context, isMine: Boolean, timenoteListenerListener: TimenoteOptionsListener){
            var listItems = mutableListOf<String>()
            if(isMine) listItems = mutableListOf(context.getString(R.string.duplicate), context.getString(
                R.string.edit), context.getString(R.string.delete), context.getString(R.string.alarm))
            else listItems = mutableListOf(context.getString(R.string.duplicate), context.getString(R.string.delete), context.getString(R.string.alarm), context.getString(R.string.report), context.getString(
                R.string.hide_to_others))
            MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(R.string.posted_false)
                listItems (items = listItems){ dialog, index, text ->
                    when(text.toString()){
                        context.getString(R.string.duplicate) -> timenoteListenerListener.onDuplicateClicked()
                        context.getString(R.string.edit) -> timenoteListenerListener.onEditClicked()
                        context.getString(R.string.report) -> timenoteListenerListener.onReportClicked()
                        context.getString(R.string.alarm) -> timenoteListenerListener.onAlarmClicked()
                        context.getString(R.string.delete) -> timenoteListenerListener.onDeleteClicked()
                        context.getString(R.string.hide_to_others) -> timenoteListenerListener.onHideToOthersClicked()
                    }
                }
            }
        }

        fun showFilterBar(onRemoveFilterBarListener: OnRemoveFilterBarListener) {
            val chips = mutableListOf("With Alarm", "My Timenotes", "The Liked", "Tagged Ones", "With Note")
            val chipProfileFilterAdapter = ProfileFilterChipAdapter(chips, onRemoveFilterBarListener)
            itemView.profile_filter_rv_chips_in_rv.apply {
                layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
                adapter = chipProfileFilterAdapter
            }
        }
    }

}