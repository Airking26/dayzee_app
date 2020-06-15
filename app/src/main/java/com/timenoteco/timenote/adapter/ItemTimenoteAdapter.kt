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
import kotlinx.android.synthetic.main.item_timenote.view.*
import kotlinx.android.synthetic.main.item_timenote_recent.view.*

class ItemTimenoteAdapter(val timenotes: List<Timenote>, val timenotesToCome: List<Timenote>,
                          val isHeterogeneous: Boolean, val commentListener: CommentListener, val plusListener: PlusListener)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    interface CommentListener{
        fun onCommentClicked()
    }

    interface PlusListener{
        fun onPlusClicked()
    }

    var itemViewType: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(isHeterogeneous) {
            when (viewType) {
                0 -> TimenoteToComeViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.adapter_timenote_recent, parent, false)
                )
                else -> TimenoteViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_timenote, parent, false)
                )
            }
        } else {
            TimenoteViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_timenote, parent, false))
        }

    }

    override fun getItemCount(): Int{
        return if(isHeterogeneous) {
            when (itemViewType) {
                0 -> timenotesToCome.size
                else -> timenotes.size
            }
        } else {
            timenotes.size
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(isHeterogeneous){
            when(itemViewType){
                0 -> (holder as TimenoteToComeViewHolder).bindTimenoteTocome(timenotesToCome)
                else -> (holder as TimenoteViewHolder).bindTimenote(timenotes[position], commentListener, plusListener)
            }
        } else {
            (holder as TimenoteViewHolder).bindTimenote(
                timenotes[position],
                commentListener,
                plusListener
            )
        }

    }


    override fun getItemViewType(position: Int): Int {
        return if(isHeterogeneous) {
            itemViewType = position % 4 * 5
            itemViewType
        } else {
            super.getItemViewType(position)
        }
    }

    class TimenoteViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {


        fun bindTimenote(
            timenote: Timenote,
            commentListener: CommentListener,
            plusListener: PlusListener
        ) {

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
            itemView.timenote_day_month.setOnClickListener {
                itemView.separator_1.visibility = View.GONE
                itemView.separator_2.visibility = View.GONE
                itemView.timenote_day_month.visibility = View.GONE
                itemView.timenote_time.visibility = View.GONE
                itemView.timenote_year.visibility = View.GONE
                itemView.timerProgramCountdown.visibility = View.VISIBLE
                itemView.timerProgramCountdown.startCountDown(99999999)
            }
            itemView.timenote_comment.setOnClickListener { commentListener.onCommentClicked() }
            itemView.timenote_plus.setOnClickListener { plusListener.onPlusClicked() }

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
            itemView.timenote_recent_date.text = timenote.dateIn
        }
    }

}