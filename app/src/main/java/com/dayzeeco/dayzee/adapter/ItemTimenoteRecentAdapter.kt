package com.dayzeeco.dayzee.adapter

import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.CountDownTimer
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
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.common.Utils
import com.dayzeeco.dayzee.model.TimenoteInfoDTO
import kotlinx.android.synthetic.main.item_timenote_recent.view.*
import kotlinx.android.synthetic.main.item_timenote_root.view.*
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.time.ExperimentalTime

class ItemTimenoteRecentAdapter(private val timenotesToCome: List<TimenoteInfoDTO>, val timenoteRecentClicked: TimenoteRecentClicked, val utils: Utils):
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
        holder.bindItem(timenotesToCome[position], timenoteRecentClicked, utils)
    }


    class TimenoteToComeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var timer : CountDownTimer? = null

        @ExperimentalTime
        @RequiresApi(Build.VERSION_CODES.O)
        fun bindItem(timenote: TimenoteInfoDTO, timenoteClicked: TimenoteRecentClicked, utils: Utils){

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
            val duration = Duration.between(Instant.now(), Instant.parse(timenote.startingAt)).toMillis()
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = duration
            if(timer != null){
                timer!!.cancel()
            }
            timer = object: CountDownTimer(duration, 1000){
                override fun onTick(millisUntilFinished: Long) {
                    val years = calendar[Calendar.YEAR] - 1970
                    val months = calendar[Calendar.MONTH]
                    var valueToSub: Int
                    if(months == 0) valueToSub =  1 else valueToSub = months
                    val daysToSubstract = calendar[Calendar.DAY_OF_MONTH] - valueToSub
                    val hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished) - TimeUnit.DAYS.toHours(
                        TimeUnit.MILLISECONDS.toDays(millisUntilFinished))
                    val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished))
                    val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
                    itemView.timenote_recent_date.text = utils.formatInTime(years.toLong(), months.toLong(), daysToSubstract.toLong(),hours, minutes, seconds, itemView.context)
                }

                override fun onFinish() {
                    itemView.timenote_recent_date.text =  itemView.context.getString(R.string.live)
                }

            }.start()
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