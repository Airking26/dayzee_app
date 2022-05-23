package com.dayzeeco.dayzee.adapter

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
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
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.common.Utils
import com.dayzeeco.dayzee.listeners.ItemProfileCardListener
import com.dayzeeco.dayzee.listeners.TimenoteOptionsListener
import com.dayzeeco.dayzee.model.AlarmInfoDTO
import com.dayzeeco.dayzee.model.TimenoteInfoDTO
import kotlinx.android.synthetic.main.item_profile_timenote_list_style.view.*
import kotlinx.android.synthetic.main.item_profile_timenote_list_style.view.profile_item_address_event
import kotlinx.android.synthetic.main.item_timenote.view.*
import kotlinx.android.synthetic.main.item_timenote_root.view.*
import java.text.SimpleDateFormat
import java.time.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.time.ExperimentalTime

val allSelectedProfile : MutableList<Int> = mutableListOf()

class ProfileEventPagingAdapter(diffUtilCallback: DiffUtil.ItemCallback<TimenoteInfoDTO>,
                                private val timenoteOptionsListener: TimenoteOptionsListener,
                                private val onCardClicked: ItemProfileCardListener,
                                private val isMine: String?, private val isUpcoming: Boolean,
                                private val listOfAlarms: MutableList<AlarmInfoDTO>, private val isOnMyProfile: Boolean, private val utils: Utils)
    : PagingDataAdapter<TimenoteInfoDTO, RecyclerView.ViewHolder>(diffUtilCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return TimenoteListHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_profile_timenote_list_style, parent, false))

    }

    @OptIn(ExperimentalTime::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as TimenoteListHolder).bindListStyleItem(
                getItem(position)!!,
                timenoteOptionsListener,
                onCardClicked,
                isMine, isUpcoming,
                listOfAlarms, isOnMyProfile, utils)
    }

    fun resetAllSelected(){
        allSelectedProfile.clear()
    }


    override fun getItemViewType(position: Int): Int {
            return R.layout.item_profile_timenote_list_style
    }

}

class TimenoteListHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    var timer : CountDownTimer? = null

    @ExperimentalTime
    @RequiresApi(Build.VERSION_CODES.O)
    fun bindListStyleItem(
        event: TimenoteInfoDTO,
        timenoteOptionsListener: TimenoteOptionsListener,
        onCardClicked: ItemProfileCardListener,
        isMine: String?,
        isUpcoming: Boolean,
        listOfAlarms: MutableList<AlarmInfoDTO>,
        isOnMyProfile: Boolean,
        utils: Utils
    ) {
        val DATE_FORMAT_DAY_AND_TIME = "EEE, d MMM yyyy hh:mm aaa"
        val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

        itemView.setOnClickListener { onCardClicked.onCardClicked(event) }

        if(event.video.isNullOrEmpty()) itemView.profile_item_is_video.visibility = View.GONE
        else itemView.profile_item_is_video.visibility = View.VISIBLE

        itemView.profile_item_name_event.text = event.title
        if(event.location != null) {
            if(event.location.address.address.isEmpty() && event.location.address.city.isNotEmpty() && event.location.address.country.isNotEmpty()){
                itemView.profile_item_address_event.text = event.location.address.city.plus(" ").plus(event.location.address.country)
            }
            else if(event.location.address.address.isNotEmpty() && event.location.address.city.isNotEmpty() && event.location.address.country.isNotEmpty() ) {
                itemView.profile_item_address_event.text = event.location.address.address.plus(", ")
                    .plus(event.location.address.city).plus(" ")
                    .plus(event.location.address.country)
            }
            else itemView.profile_item_address_event.text = event.location.address.address
        }
        else itemView.timenote_place?.text = ""
        itemView.profile_item_name_owner.text = event.createdBy.userName


        if(isUpcoming) {
            val o = SimpleDateFormat(ISO)
            o.timeZone = TimeZone.getTimeZone("UTC")
            val m = o.parse(event.startingAt)
            o.timeZone = TimeZone.getDefault()

            val duration = Duration.between(Instant.now(), Instant.parse(event.startingAt)).toMillis()
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
                            LocalDateTime.ofInstant(Instant.parse(event.startingAt), ZoneOffset.UTC).toLocalDate()
                        )

                        years = period.years.toLong()
                        months = period.minusYears(years).months.toLong()
                        days = if(TimeUnit.MILLISECONDS.toDays(millisUntilFinished) < period.minusYears(years).minusMonths(months).days.toLong()) TimeUnit.MILLISECONDS.toDays(millisUntilFinished) else period.minusYears(years).minusMonths(months).days.toLong()
                        hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millisUntilFinished))
                        minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))
                        seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
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
                   itemView.profile_item_date_event.text = if(allSelectedProfile.contains(absoluteAdapterPosition)) SimpleDateFormat(DATE_FORMAT_DAY_AND_TIME, Locale.getDefault()).format(m.time) else utils.formatInTime(years, months, days,hours, minutes, seconds, itemView.context)
                }

                override fun onFinish() {
                    itemView.profile_item_date_event.text =  itemView.context.getString(R.string.live)
                }

            }.start()
        } else itemView.profile_item_date_event.text = Utils().sinceTime(event.endingAt, itemView.context)


        itemView.profile_item_date_event.setOnClickListener {
            val o = SimpleDateFormat(ISO)
            o.timeZone = TimeZone.getTimeZone("UTC")
            val m = o.parse(event.startingAt)
            o.timeZone = TimeZone.getDefault()
            if(itemView.profile_item_date_event.text.contains(itemView.context.getString(R.string.in_time), true) || itemView.profile_item_date_event.text.contains(itemView.context.getString(R.string.since_time), true)){
                allSelectedProfile.add(absoluteAdapterPosition)
                itemView.profile_item_date_event.text = SimpleDateFormat(DATE_FORMAT_DAY_AND_TIME, Locale.getDefault()).format(m.time)
            } else {
                allSelectedProfile.remove(absoluteAdapterPosition)
                if(isUpcoming){
                    val duration = Duration.between(Instant.now(), Instant.parse(event.startingAt)).toMillis()
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
                                    LocalDateTime.ofInstant(Instant.parse(event.startingAt), ZoneOffset.UTC).toLocalDate()
                                )

                                years = period.years.toLong()
                                months = period.minusYears(years).months.toLong()
                                days = if(TimeUnit.MILLISECONDS.toDays(millisUntilFinished) < period.minusYears(years).minusMonths(months).days.toLong()) TimeUnit.MILLISECONDS.toDays(millisUntilFinished) else period.minusYears(years).minusMonths(months).days.toLong()
                                hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millisUntilFinished))
                                minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))
                                seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
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
                            itemView.profile_item_date_event.text = if(allSelectedProfile.contains(absoluteAdapterPosition)) SimpleDateFormat(DATE_FORMAT_DAY_AND_TIME, Locale.getDefault()).format(m.time) else utils.formatInTime(years, months, days,hours, minutes, seconds, itemView.context)
                        }

                        override fun onFinish() {
                            itemView.profile_item_date_event.text =  itemView.context.getString(R.string.live)
                        }

                    }.start()
                } else itemView.profile_item_date_event.text = Utils().sinceTime(event.endingAt, itemView.context)
            }
        }
        itemView.profile_item_options.setOnClickListener { createOptionsOnTimenote(itemView.context, isMine, timenoteOptionsListener, event, listOfAlarms, isOnMyProfile) }
        if(!event.pictures.isNullOrEmpty()) {
            Glide
                .with(itemView)
                .load(event.pictures[0])
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(itemView.profile_item_pic_event_imageview)
        } else if(!event.colorHex.isNullOrBlank() && !event.colorHex.isNullOrEmpty()) itemView.profile_item_pic_event_imageview.setBackgroundColor(Color.parseColor(if(event.colorHex?.contains("#")!!) event.colorHex else "#${event.colorHex}"))

        if (event.createdBy.picture.isNullOrBlank()){
            itemView.profile_item_pic_profile_imageview.setImageDrawable(utils.determineLetterLogo(event.createdBy.userName!!, itemView.context))
        } else Glide
            .with(itemView)
            .load(event.createdBy.picture)
            .apply(RequestOptions.circleCropTransform())
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .placeholder(R.drawable.circle_pic)
            .into(itemView.profile_item_pic_profile_imageview)
    }
    private fun createOptionsOnTimenote(
        context: Context,
        isMine: String?,
        timenoteListenerListener: TimenoteOptionsListener,
        event: TimenoteInfoDTO,
        listOfAlarms: MutableList<AlarmInfoDTO>,
        isOnMyProfile: Boolean
    ){
        val dateFormat = SimpleDateFormat("dd.MM.yyyy")
        val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        var li: MutableList<String> = mutableListOf()

        if(isOnMyProfile) {
            if(listOfAlarms.isEmpty()){
                    li = if (isMine!! == event.createdBy.id) {
                        mutableListOf(
                            context.getString(R.string.share_to),
                            context.getString(R.string.create_alarm),
                            context.getString(R.string.duplicate),
                            context.getString(
                                R.string.edit
                            ),
                            context.getString(R.string.delete)
                        )
                    } else {
                        mutableListOf(
                            context.getString(R.string.share_to),
                            context.getString(R.string.create_alarm),
                            context.getString(R.string.hide_post),
                            context.getString(R.string.hide_all_posts),
                            context.getString(R.string.duplicate),
                            context.getString(R.string.report),
                            context.getString(
                                R.string.hide_to_others
                            )
                        )
                    }
            } else for (alrm in listOfAlarms) {
                if (alrm.timenote == event.id) {
                    if (isMine!! == event.createdBy.id) {
                        li = mutableListOf(
                            context.getString(R.string.share_to),
                            context.getString(R.string.update_alarm),
                            context.getString(R.string.delete_alarm),
                            context.getString(R.string.duplicate),
                            context.getString(R.string.edit),
                            context.getString(R.string.delete)
                        )
                        break
                    } else {
                        li = mutableListOf(
                            context.getString(R.string.share_to),
                            context.getString(R.string.hide_post),
                            context.getString(R.string.hide_all_posts),
                            context.getString(R.string.update_alarm),
                            context.getString(R.string.delete_alarm),
                            context.getString(R.string.duplicate),
                            context.getString(R.string.report),
                            context.getString(R.string.hide_to_others)
                        )
                        break
                    }
                } else {
                    li = if (isMine!! == event.createdBy.id) {
                        mutableListOf(
                            context.getString(R.string.share_to),
                            context.getString(R.string.create_alarm),
                            context.getString(R.string.duplicate),
                            context.getString(
                                R.string.edit
                            ),
                            context.getString(R.string.delete)
                        )
                    } else {
                        mutableListOf(
                            context.getString(R.string.share_to),
                            context.getString(R.string.create_alarm),
                            context.getString(R.string.hide_post),
                            context.getString(R.string.hide_all_posts),
                            context.getString(R.string.duplicate),
                            context.getString(R.string.report),
                            context.getString(
                                R.string.hide_to_others
                            )
                        )
                    }
                }
            }
        } else {
            li = if (isMine!! == event.createdBy.id) {
                mutableListOf(
                    context.getString(R.string.share_to),
                    context.getString(R.string.duplicate),
                    context.getString(
                        R.string.edit
                    ),
                    context.getString(R.string.delete)
                )
            } else {
                mutableListOf(
                    context.getString(R.string.share_to),
                    context.getString(R.string.hide_post),
                    context.getString(R.string.hide_all_posts),
                    context.getString(R.string.duplicate),
                    context.getString(R.string.report)

                )
            }
        }

        MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(text = dateFormat.format(SimpleDateFormat(ISO).parse(event.createdAt).time))
            listItems (items = li){ _, _, text ->
                when(text.toString()){
                    context.getString(R.string.share_to) -> timenoteListenerListener.onShareClicked(event)
                    context.getString(R.string.duplicate) -> timenoteListenerListener.onDuplicateClicked(event)
                    context.getString(R.string.edit) -> timenoteListenerListener.onEditClicked(event)
                    context.getString(R.string.report) -> timenoteListenerListener.onReportClicked(event)
                    context.getString(R.string.create_alarm) -> timenoteListenerListener.onAlarmClicked(event, 0)
                    context.getString(R.string.update_alarm) -> timenoteListenerListener.onAlarmClicked(event, 1)
                    context.getString(R.string.delete_alarm) -> timenoteListenerListener.onAlarmClicked(event, 2)
                    context.getString(R.string.delete) -> timenoteListenerListener.onDeleteClicked(event)
                    context.getString(R.string.hide_to_others) -> timenoteListenerListener.onHideToOthersClicked(event)
                    context.getString(R.string.hide_post) -> timenoteListenerListener.onHidePostClicked(event, absoluteAdapterPosition)
                    context.getString(R.string.hide_all_posts) -> timenoteListenerListener.onHideUserClicked(event, absoluteAdapterPosition)
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