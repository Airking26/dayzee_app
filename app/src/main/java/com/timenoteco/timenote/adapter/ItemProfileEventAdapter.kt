package com.timenoteco.timenote.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.list.listItems
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.timenoteco.timenote.R
import com.timenoteco.timenote.common.Utils
import com.timenoteco.timenote.common.onItemClick
import com.timenoteco.timenote.listeners.ItemProfileCardListener
import com.timenoteco.timenote.listeners.OnRemoveFilterBarListener
import com.timenoteco.timenote.listeners.TimenoteOptionsListener
import com.timenoteco.timenote.model.TimenoteInfoDTO
import kotlinx.android.synthetic.main.fragment_filter.view.*
import kotlinx.android.synthetic.main.item_profile_timenote_list_style.view.*
import java.text.SimpleDateFormat
import java.util.*


class ItemProfileEventAdapter(
    private var events: List<TimenoteInfoDTO>,
    private val timenoteOptionsListener: TimenoteOptionsListener,
    private val onRemoveFilterBarListener: OnRemoveFilterBarListener,
    private val onCardClicked: ItemProfileCardListener,
    private var showHideFilterBar: Boolean,
    private val isMine: String?
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
        (holder as TimenoteListHolder).bindListStyleItem(events[position], timenoteOptionsListener, onCardClicked, isMine)
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == 0 && showHideFilterBar) R.layout.fragment_filter else R.layout.item_profile_timenote_list_style
    }

    class TimenoteListHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindListStyleItem(event: TimenoteInfoDTO, timenoteOptionsListener: TimenoteOptionsListener, onCardClicked: ItemProfileCardListener, isMine: String?) {

            itemView.setOnClickListener { onCardClicked.onCardClicked(event) }

            itemView.profile_item_name_event.text = event.title
            if(event.location != null)
            itemView.profile_item_address_event.text = event.location.address.address.plus(", ").plus(event.location.address.city).plus(" ").plus(event.location.address.country)
            itemView.profile_item_name_owner.text = event.createdBy.userName
            itemView.profile_item_date_event.text = Utils().calculateDecountTime(event.startingAt)
            itemView.profile_item_options.setOnClickListener { createOptionsOnTimenote(itemView.context, isMine, timenoteOptionsListener, event) }
            if(!event.pictures.isNullOrEmpty()) {
                Glide
                    .with(itemView)
                    .load(event.pictures[0])
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(itemView.profile_item_pic_event_imageview)
            } else itemView.profile_item_pic_event_imageview.setBackgroundColor(Color.parseColor(event.colorHex))

            Glide
                .with(itemView)
                .load(event.createdBy.picture)
                .apply(RequestOptions.circleCropTransform())
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.circle_pic)
                .into(itemView.profile_item_pic_profile_imageview)
        }
        private fun createOptionsOnTimenote(context: Context, isMine: String?, timenoteListenerListener: TimenoteOptionsListener, event: TimenoteInfoDTO){
            val dateFormat = SimpleDateFormat("dd.MM.yyyy")
            val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
            val listItems: MutableList<String> =
                if(isMine!! == event.createdBy.id) mutableListOf(context.getString(R.string.duplicate), context.getString(
                R.string.edit), context.getString(R.string.delete), context.getString(R.string.alarm))
                else mutableListOf(context.getString(R.string.duplicate), context.getString(R.string.alarm), context.getString(R.string.report), context.getString(
                R.string.hide_to_others))
            MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                title(text = "Posted : " + dateFormat.format(SimpleDateFormat(ISO).parse(event.createdAt).time))
                listItems (items = listItems){ _, _, text ->
                    when(text.toString()){
                        context.getString(R.string.duplicate) -> timenoteListenerListener.onDuplicateClicked(event)
                        context.getString(R.string.edit) -> timenoteListenerListener.onEditClicked(event)
                        context.getString(R.string.report) -> timenoteListenerListener.onReportClicked()
                        context.getString(R.string.alarm) -> timenoteListenerListener.onAlarmClicked(event)
                        context.getString(R.string.delete) -> timenoteListenerListener.onDeleteClicked(event)
                        context.getString(R.string.hide_to_others) -> timenoteListenerListener.onHideToOthersClicked(event)
                    }
                }
            }
        }

        fun showFilterBar(onRemoveFilterBarListener: OnRemoveFilterBarListener) {
            val chips = mutableListOf("My Timenotes", "The Joined", "With Alarm", "Group Related", "Other")
            val chipProfileFilterAdapter = ProfileFilterChipAdapter(chips, onRemoveFilterBarListener)
            itemView.profile_filter_rv_chips_in_rv.apply {
                layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
                adapter = chipProfileFilterAdapter
                //onItemClick { recyclerView, position, v -> }
            }
        }
    }

}