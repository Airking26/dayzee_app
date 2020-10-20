package com.timenoteco.timenote.adapter

import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.text.TextPaint
import android.text.style.MetricAffectingSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.timenoteco.timenote.R
import com.timenoteco.timenote.common.Utils
import com.timenoteco.timenote.model.TimenoteInfoDTO
import kotlinx.android.synthetic.main.item_timenote_recent.view.*

class ItemTimenoteRecentAdapter(private val timenotesToCome: List<TimenoteInfoDTO>, val timenoteRecentClicked: TimenoteRecentClicked):
    RecyclerView.Adapter<ItemTimenoteRecentAdapter.TimenoteToComeViewHolder>(){

    interface TimenoteRecentClicked{
        fun onTimenoteRecentClicked(event: TimenoteInfoDTO)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimenoteToComeViewHolder {
        return TimenoteToComeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_timenote_recent, parent, false))
    }

    override fun getItemCount(): Int {
        return timenotesToCome.size
    }

    override fun onBindViewHolder(holder: TimenoteToComeViewHolder, position: Int) {
        holder.bindItem(timenotesToCome[position], timenoteRecentClicked)
    }


    class TimenoteToComeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bindItem(timenote: TimenoteInfoDTO, timenoteClicked: TimenoteRecentClicked){
            if(timenote.pictures.isNullOrEmpty()){
                itemView.timenote_recent_pic_imageview.setBackgroundColor(Color.parseColor(timenote.colorHex))
            } else {
                Glide
                    .with(itemView)
                    .load(timenote.pictures[0])
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .centerCrop()
                    .placeholder(R.drawable.loader)
                    .into(itemView.timenote_recent_pic_imageview)
            }

            Glide
                .with(itemView)
                .load(timenote.createdBy.picture)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.circle_pic)
                .into(itemView.timenote_recent_pic_user_imageview)

            itemView.timenote_recent_title.text = timenote.title
            itemView.timenote_recent_date.text = Utils().inTime(timenote.startingAt)
            itemView.setOnClickListener { timenoteClicked.onTimenoteRecentClicked(timenote) }
        }
    }

    class CustomTypefaceSpan(private val typeface: Typeface?) : MetricAffectingSpan() {
        override fun updateDrawState(paint: TextPaint) {
            paint.typeface = typeface
        }

        override fun updateMeasureState(paint: TextPaint) {
            paint.typeface = typeface
        }
    }


}