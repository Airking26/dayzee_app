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
import java.text.SimpleDateFormat
import java.time.*
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

            if (timenote.createdBy.picture.isNullOrBlank()){
                itemView.timenote_recent_pic_user_imageview.setImageDrawable(utils.determineLetterLogo(timenote.createdBy.userName!!, itemView.context))
            } else Glide
                .with(itemView)
                .load(timenote.createdBy.picture)
                .thumbnail(0.1f)
                .placeholder(R.drawable.circle_pic)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .apply(RequestOptions.circleCropTransform())
                .into(itemView.timenote_recent_pic_user_imageview)


            val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
            if(utils.inTime(timenote.startingAt, itemView.context) != itemView.context.getString(R.string.live)) itemView.timenote_recent_date.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0,0, 0)
            else itemView.timenote_recent_date.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_oval, 0,0, 0)
            var duration: Long = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Duration.between(
                Instant.now(), Instant.parse(timenote.startingAt)).toMillis()
            else SimpleDateFormat(ISO).parse(timenote.startingAt).time - System.currentTimeMillis()
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = duration
            if(timer != null){
                timer!!.cancel()
            }
            timer = object: CountDownTimer(duration, 1000){
                override fun onTick(millisUntilFinished: Long) {
                    val years : Long
                    val months: Long
                    val days : Long
                    val hours: Long
                    val minutes: Long
                    val seconds: Long
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val period = Period.between(
                            LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC).toLocalDate(),
                            LocalDateTime.ofInstant(Instant.parse(timenote.startingAt), ZoneOffset.UTC).toLocalDate()
                        )

                        years = period.years.toLong()
                        months = period.minusYears(years).months.toLong()
                        days = if(TimeUnit.MILLISECONDS.toDays(millisUntilFinished) < period.minusYears(years).minusMonths(months).days.toLong()) TimeUnit.MILLISECONDS.toDays(millisUntilFinished) else period.minusYears(years).minusMonths(months).days.toLong()
                        hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished) - TimeUnit.DAYS.toHours(
                            TimeUnit.MILLISECONDS.toDays(millisUntilFinished))
                        minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished))
                        seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
                    } else {
                        val calendarLocal = Calendar.getInstance()
                        calendarLocal.timeInMillis = millisUntilFinished
                        years = (calendarLocal.get(Calendar.YEAR) - 1970).toLong()
                        months = (calendarLocal.get(Calendar.MONTH)).toLong()
                        days = (calendarLocal.get(Calendar.DAY_OF_MONTH) - 1).toLong()
                        hours = (calendarLocal.get(Calendar.HOUR) + 12).toLong()
                        minutes = (calendarLocal.get(Calendar.MINUTE)).toLong()
                        seconds = (calendarLocal.get(Calendar.SECOND)).toLong()
                    }


                    itemView.timenote_recent_date.text = utils.formatInTime(years, months, days,hours, minutes, seconds, itemView.context)
                }

                override fun onFinish() {
                    itemView.timenote_recent_date.text =  itemView.context.getString(R.string.live)
                }

            }.start()

            itemView.timenote_recent_title.text = timenote.title
            //if(Utils().inTime(timenote.startingAt) == "LIVE") itemView.timenote_in_label.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_oval, 0,0, 0)
            //else itemView.timenote_in_label.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0,0, 0)
            //itemView.timenote_recent_date.text = Utils().inTime(timenote.startingAt, itemView.context)
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