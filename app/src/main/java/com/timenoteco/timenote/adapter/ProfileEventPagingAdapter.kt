package com.timenoteco.timenote.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
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
import com.timenoteco.timenote.listeners.ItemProfileCardListener
import com.timenoteco.timenote.listeners.TimenoteOptionsListener
import com.timenoteco.timenote.model.TimenoteInfoDTO
import kotlinx.android.synthetic.main.item_profile_timenote_list_style.view.*
import java.text.SimpleDateFormat

class ProfileEventPagingAdapter(diffUtilCallback: DiffUtil.ItemCallback<TimenoteInfoDTO>,
                                private val timenoteOptionsListener: TimenoteOptionsListener,
                                private val onCardClicked: ItemProfileCardListener,
                                private val isMine: String?, private val isUpcoming: Boolean)
    : PagingDataAdapter<TimenoteInfoDTO, RecyclerView.ViewHolder>(diffUtilCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return TimenoteListHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_profile_timenote_list_style, parent, false))

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as TimenoteListHolder).bindListStyleItem(
                getItem(position)!!,
                timenoteOptionsListener,
                onCardClicked,
                isMine, isUpcoming)
    }


    override fun getItemViewType(position: Int): Int {
            return R.layout.item_profile_timenote_list_style
    }

}

class TimenoteListHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    fun bindListStyleItem(event: TimenoteInfoDTO, timenoteOptionsListener: TimenoteOptionsListener, onCardClicked: ItemProfileCardListener, isMine: String?, isUpcoming: Boolean) {

        itemView.setOnClickListener { onCardClicked.onCardClicked(event) }

        itemView.profile_item_name_event.text = event.title
        if(event.location != null)
            itemView.profile_item_address_event.text = event.location.address.address.plus(", ").plus(event.location.address.city).plus(" ").plus(event.location.address.country)
        itemView.profile_item_name_owner.text = event.createdBy.userName
        itemView.profile_item_date_event.text = if(isUpcoming) Utils().inTime(event.startingAt) else Utils().sinceTime(event.endingAt)
        itemView.profile_item_options.setOnClickListener { createOptionsOnTimenote(itemView.context, isMine, timenoteOptionsListener, event) }
        if(!event.pictures.isNullOrEmpty()) {
            Glide
                .with(itemView)
                .load(event.pictures[0])
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(itemView.profile_item_pic_event_imageview)
        } else itemView.profile_item_pic_event_imageview.setBackgroundColor(Color.parseColor(if(event.colorHex?.contains("#")!!) event.colorHex else "#${event.colorHex}"))

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
}


object ProfileEventComparator : DiffUtil.ItemCallback<TimenoteInfoDTO>(){

    override fun areItemsTheSame(oldItem: TimenoteInfoDTO, newItem: TimenoteInfoDTO): Boolean {
        return oldItem.startingAt == newItem.startingAt
    }

    override fun areContentsTheSame(oldItem: TimenoteInfoDTO, newItem: TimenoteInfoDTO): Boolean {
        return oldItem == newItem
    }

}