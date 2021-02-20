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
import kotlinx.android.synthetic.main.item_timenote_root.view.*
import kotlin.time.ExperimentalTime

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

    @ExperimentalTime
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TimenoteToComeViewHolder, position: Int) {
        holder.bindItem(timenotesToCome[position], timenoteRecentClicked)
    }


    class TimenoteToComeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        @ExperimentalTime
        @RequiresApi(Build.VERSION_CODES.O)
        fun bindItem(timenote: TimenoteInfoDTO, timenoteClicked: TimenoteRecentClicked){

            if(timenote.pictures.isNullOrEmpty()){
                if(!timenote.colorHex.isNullOrEmpty() && !timenote.colorHex.isNullOrBlank())
                itemView.timenote_recent_pic_imageview.setBackgroundColor(Color.parseColor(if(timenote.colorHex.contains("#")) timenote.colorHex else  "#${timenote.colorHex}"))
            } else {
                Glide
                    .with(itemView)
                    .load(timenote.pictures[0])
                    .thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(itemView.timenote_recent_pic_imageview)
            }

            Glide
                .with(itemView)
                .load(timenote.createdBy.picture)
                .thumbnail(0.1f)
                .placeholder(R.drawable.circle_pic)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .apply(RequestOptions.circleCropTransform())
                .into(itemView.timenote_recent_pic_user_imageview)

            itemView.timenote_recent_title.text = timenote.title
            //if(Utils().inTime(timenote.startingAt) == "LIVE") itemView.timenote_in_label.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_oval, 0,0, 0)
            //else itemView.timenote_in_label.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0,0, 0)
            itemView.timenote_recent_date.text = Utils().inTime(timenote.startingAt, itemView.context)
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