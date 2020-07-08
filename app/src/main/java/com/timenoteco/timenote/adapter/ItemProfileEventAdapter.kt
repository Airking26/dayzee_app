package com.timenoteco.timenote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.timenoteco.timenote.R
import com.timenoteco.timenote.model.Timenote
import kotlinx.android.synthetic.main.item_profile_timenote_list_style.view.*
import kotlinx.android.synthetic.main.item_timenote.view.*

class ItemProfileEventAdapter(private var events: List<Timenote>, private val fragment: Fragment): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
            0 -> (holder as TimenoteListHolder).bindListStyleItem(events[position])
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
        fun bindListStyleItem(event: Timenote) {
            itemView.profile_item_name_event.text = event.title
            itemView.profile_item_address_event.text = event.place
            itemView.profile_item_date_event.text = event.dateIn
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

            val screenSlideCreationTimenotePagerAdapter = ScreenSlideTimenotePagerAdapter(fragment, timenote.pic, true)
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