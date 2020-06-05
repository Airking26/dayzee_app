package com.timenoteco.timenote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.timenoteco.timenote.R
import com.timenoteco.timenote.model.Timenote
import kotlinx.android.synthetic.main.adapter_timenote_recent.view.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_signup.view.*
import kotlinx.android.synthetic.main.item_profile_calendar.view.*
import kotlinx.android.synthetic.main.item_timenote.view.*
import kotlinx.android.synthetic.main.item_timenote_recent.view.*

class ItemTimenoteRegularAdapter(val timenotes: List<Timenote>, val timenotesToCome: List<Timenote>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var itemViewType: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            0 -> TimenoteToComeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_timenote_recent, parent, false))
            else -> TimenoteViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_timenote, parent, false))
        }

    }

    override fun getItemCount(): Int{
        return when(itemViewType){
            0 -> timenotesToCome.size
            else -> timenotes.size
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(itemViewType){
            0 -> (holder as TimenoteToComeViewHolder).bindTimenoteTocome(timenotesToCome)
            else -> (holder as TimenoteViewHolder).bindTimenote(timenotes[position])
        }
    }


    override fun getItemViewType(position: Int): Int {
        itemViewType = position % 4 * 5
        return itemViewType
    }

    class TimenoteViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {


        fun bindTimenote(timenote: Timenote) {

            Glide
                .with(itemView)
                .load(timenote.pic_user)
                .apply(RequestOptions.circleCropTransform())
                .into(itemView.timenote_pic_user_imageview)

            Glide
                .with(itemView)
                .load(timenote.pic)
                .centerCrop()
                .into(itemView.timenote_pic_imageview)

            itemView.timenote_username.text = timenote.username
            itemView.timenote_place.text = timenote.place
            itemView.timenote_username_desc.text = timenote.desc
            itemView.timenote_title.text = timenote.title
            itemView.timenote_year.text = timenote.year
            itemView.timenote_day_month.text = timenote.month
            itemView.timenote_time.text = timenote.date

        }

    }

    class TimenoteToComeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        fun bindTimenoteTocome(timenote: List<Timenote>){
            itemView.home_recent_rv.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            itemView.home_recent_rv.adapter = ItemTimenoteToComeAdapter(timenote)
        }
    }

}

class ItemTimenoteToComeAdapter(val timenotesToCome: List<Timenote>): RecyclerView.Adapter<ItemTimenoteToComeAdapter.ItemAdapter>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAdapter {
        return ItemAdapter(LayoutInflater.from(parent.context).inflate(R.layout.item_timenote_recent, parent, false))
    }

    override fun getItemCount(): Int {
        return timenotesToCome.size
    }

    override fun onBindViewHolder(holder: ItemAdapter, position: Int) {
        holder.bindItem(timenotesToCome[position])
    }


    class ItemAdapter(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindItem(timenote: Timenote){
            Glide
                .with(itemView)
                .load(timenote.pic)
                .centerCrop()
                .into(itemView.timenote_recent_pic_imageview)

            itemView.timenote_recent_title.text = timenote.title
            itemView.timenote_recent_date.text = timenote.date
        }
    }

}