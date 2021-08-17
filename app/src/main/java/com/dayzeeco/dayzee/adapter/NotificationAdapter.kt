package com.dayzeeco.dayzee.adapter

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.model.NotificationInfoDTO
import com.dayzeeco.dayzee.model.TypeOfNotification
import kotlinx.android.synthetic.main.item_notification.view.*
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*

class NotificationAdapter(
    private val notifications: MutableList<NotificationInfoDTO>,
    private val notificationClickListener: NotificationClickListener
): RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    interface NotificationClickListener{
        fun onNotificationClicked(notification: NotificationInfoDTO)
        fun onAcceptedRequestClicked(notification: NotificationInfoDTO)
        fun onDeclinedRequestClicked(notification: NotificationInfoDTO)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder =
        NotificationViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_notification,
                parent,
                false
            )
        )

    override fun getItemCount(): Int = notifications.size

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) = holder.bindNotification(
        notifications[position],
        notificationClickListener
    )

    class NotificationViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        @RequiresApi(Build.VERSION_CODES.M)
        fun bindNotification(
            notification: NotificationInfoDTO,
            notificationClickListener: NotificationClickListener
        ) {

            if(notification.type == TypeOfNotification.ASKEDTOFOLLOW.ordinal){
                itemView.notification_item_user_accept.visibility =View.VISIBLE
                itemView.notification_item_user_decline.visibility =  View.VISIBLE
            }

            if(!notification.hasBeenRead){
                itemView.setBackgroundColor(Color.parseColor("#20aaaaaa"))
            } else {
                itemView.setBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.colorBackground
                    )
                )
            }

            if(notification.type == 1) itemView.notification_user_pic_imageview.setImageDrawable(
                itemView.resources.getDrawable(
                    R.drawable.ic_baseline_notifications_active_24
                )
            )
            else Glide
                .with(itemView)
                .load(notification.picture)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.circle_pic)
                .into(itemView.notification_user_pic_imageview)

            itemView.notification_annoucement.text = when(notification.type) {
                0 -> String.format(
                    itemView.context.getString(R.string.timenote_shared_body),
                    notification.username,
                    notification.eventName
                )
                2 -> String.format(
                    itemView.context.getString(R.string.new_follower_body),
                    notification.username
                )
                3 -> String.format(
                    itemView.context.getString(R.string.asked_to_follow_body),
                    notification.username
                )
                4 -> String.format(
                    itemView.context.getString(R.string.accepted_follow_ask_body),
                    notification.username
                )
                5 -> String.format(
                    itemView.context.getString(R.string.timenote_joined_body),
                    notification.username,
                    notification.eventName
                )
                6 -> String.format(
                    itemView.context.getString(R.string.tagged_in_comment_body),
                    notification.username,
                    notification.eventName
                )
                else -> ""
            }
            itemView.notification_time.text = calculateTimeSinceNotif(notification.createdAt)
            itemView.setOnClickListener{notificationClickListener.onNotificationClicked(notification)}
            itemView.notification_item_user_accept.setOnClickListener { notificationClickListener.onAcceptedRequestClicked(
                notification
            ) }
            itemView.notification_item_user_decline.setOnClickListener { notificationClickListener.onDeclinedRequestClicked(
                notification
            ) }
        }

        private fun calculateTimeSinceNotif(createdAt: String): String{
            val ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

            val d = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Date.from(Instant.from(DateTimeFormatter.ISO_INSTANT.parse(createdAt))).time
            } else {
                val sdf = SimpleDateFormat(ISO, Locale.getDefault())
                sdf.timeZone = TimeZone.getTimeZone("GMT")
                sdf.parse(createdAt).time
            }
            val c: Calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
            c.timeInMillis = System.currentTimeMillis() + 10000 - d
            val mYear: Int = c.get(Calendar.YEAR) - 1970
            val mMonth: Int = c.get(Calendar.MONTH)
            val mDay: Int = c.get(Calendar.DAY_OF_MONTH) - 1
            val mHours: Int = c.get(Calendar.HOUR)
            val mMin : Int = c.get(Calendar.MINUTE)

            val timeSince: String
            if(mYear <= 0){
                if(mMonth <= 0){
                    if(mDay <=0){
                        timeSince = if(mHours <= 0){
                            if(mMin <= 0){
                                itemView.context.getString(R.string.few_sec_ago)
                            } else {
                                if(mMin > 1) itemView.context.resources.getQuantityString(
                                    R.plurals.time_minutes_comment,
                                    mMin,
                                    mMin
                                )
                                else itemView.context.resources.getQuantityString(
                                    R.plurals.time_minutes_comment,
                                    mMin,
                                    mMin
                                )
                            }
                        } else {
                            if(mHours > 1) itemView.context.resources.getQuantityString(
                                R.plurals.time_hours_comment,
                                mHours,
                                mHours
                            )
                            else itemView.context.resources.getQuantityString(
                                R.plurals.time_hours_comment,
                                mHours,
                                mHours
                            )
                        }
                    } else {
                        timeSince = if(mDay > 1) itemView.context.resources.getQuantityString(
                            R.plurals.time_days_comment,
                            mDay,
                            mDay
                        )
                        else itemView.context.resources.getQuantityString(
                            R.plurals.time_days_comment,
                            mDay,
                            mDay
                        )
                    }
                } else {
                    timeSince = if(mMonth > 1) itemView.context.resources.getQuantityString(
                        R.plurals.time_months_comment,
                        mMonth,
                        mMonth
                    )
                    else itemView.context.resources.getQuantityString(
                        R.plurals.time_months_comment,
                        mMonth,
                        mMonth
                    )
                }
            } else {
                timeSince = if(mYear > 1) itemView.context.resources.getQuantityString(
                    R.plurals.time_years_comment,
                    mYear,
                    mYear
                )
                else  itemView.context.resources.getQuantityString(
                    R.plurals.time_years_comment,
                    mYear,
                    mYear
                )
            }

            return timeSince
        }


    }
}