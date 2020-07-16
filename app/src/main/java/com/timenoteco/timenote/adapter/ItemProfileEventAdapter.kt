package com.timenoteco.timenote.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.list.listItems
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.timenoteco.timenote.R
import com.timenoteco.timenote.listeners.TimenoteOptionsListener
import com.timenoteco.timenote.model.Timenote
import kotlinx.android.synthetic.main.item_profile_timenote_list_style.view.*
import kotlinx.android.synthetic.main.item_timenote.view.*

class ItemProfileEventAdapter(private var events: List<Timenote>, private val fragment: Fragment, private val timenoteOptionsListener: TimenoteOptionsListener): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var style: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       return when(this.style){
           0 -> TimenoteListHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_profile_timenote_list_style, parent, false))
           else -> TimenoteGridHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_timenote, parent, false))
       }
        }

    override fun getItemCount(): Int = events.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(this.style){
            0 -> (holder as TimenoteListHolder).bindListStyleItem(events[position], timenoteOptionsListener)
            else -> (holder as TimenoteGridHolder).bindGridStyleItem(events[position], fragment)
        }
    }

    fun switchViewType() : Int{
        if(this.style == 0) this.style = 1
        else this.style = 0
        notifyDataSetChanged()
        return this.style
    }

    override fun getItemViewType(position: Int): Int {
        return this.style
    }

    class TimenoteListHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindListStyleItem(
            event: Timenote,
            timenoteOptionsListener: TimenoteOptionsListener
        ) {
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

        private fun createOptionsOnTimenote(
            context: Context,
            isMine: Boolean,
            timenoteListenerListener: TimenoteOptionsListener
        ){
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
    }
    class TimenoteGridHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bindGridStyleItem(
            timenote: Timenote,
            fragment: Fragment
        ){
            Glide
                .with(itemView)
                .load(timenote.pic_user)
                .apply(RequestOptions.circleCropTransform())
                .into(itemView.timenote_pic_user_imageview)

            val screenSlideCreationTimenotePagerAdapter = TimenoteViewPagerAdapter(
                timenote.pic,
                true
            ){}
            itemView.timenote_vp.adapter = screenSlideCreationTimenotePagerAdapter
            itemView.timenote_indicator.setViewPager(itemView.timenote_vp)
            if(timenote.pic?.size == 1) itemView.timenote_indicator.visibility = View.GONE
            screenSlideCreationTimenotePagerAdapter.registerAdapterDataObserver(itemView.timenote_indicator.adapterDataObserver)

            itemView.timenote_username.text = timenote.username
            itemView.timenote_place.text = timenote.place
            itemView.timenote_username_desc.text = timenote.desc
            itemView.timenote_title.text = timenote.title
            itemView.timenote_year.text = timenote.year
            itemView.timenote_day_month.text = timenote.month
            itemView.timenote_time.text = timenote.date
        }
    }


}