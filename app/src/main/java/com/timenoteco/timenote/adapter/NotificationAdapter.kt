package com.timenoteco.timenote.adapter

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
import com.timenoteco.timenote.R
import com.timenoteco.timenote.common.Utils
import com.timenoteco.timenote.model.Notification
import com.timenoteco.timenote.model.TypeOfNotification
import kotlinx.android.synthetic.main.item_notification.view.*
import java.util.*

class NotificationAdapter(private val notifications: MutableList<Notification>, private val notificationClickListener: NotificationClickListener): RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    interface NotificationClickListener{
        fun onNotificationClicked(notification: Notification)
        fun onAcceptedRequestClicked(notification: Notification)
        fun onDeclinedRequestClicked(notification: Notification)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder =
        NotificationViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false))

    override fun getItemCount(): Int = notifications.size

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) = holder.bindNotification(notifications[position], notificationClickListener)

    class NotificationViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        @RequiresApi(Build.VERSION_CODES.M)
        fun bindNotification(
            notification: Notification,
            notificationClickListener: NotificationClickListener
        ) {

            if(notification.type.toInt() == TypeOfNotification.ASKEDTOFOLLOW.ordinal){
                itemView.notification_item_user_accept.visibility =View.VISIBLE
                itemView.notification_item_user_decline.visibility =  View.VISIBLE
            }

            if(!notification.read){
                itemView.setBackgroundColor(Color.parseColor("#20aaaaaa"))
            } else {
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.colorBackground))
            }

            if(notification.type.toInt() == 1) itemView.notification_user_pic_imageview.setImageDrawable(itemView.resources.getDrawable(R.drawable.ic_baseline_notifications_active_24))
            else Glide
                .with(itemView)
                .load(notification.pictureUrl)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.circle_pic)
                .into(itemView.notification_user_pic_imageview)

            itemView.notification_annoucement.text = notification.body
            itemView.notification_time.text = calculateTimeSinceNotif(notification.time)
            itemView.setOnClickListener{notificationClickListener.onNotificationClicked(notification)}
            itemView.notification_item_user_accept.setOnClickListener { notificationClickListener.onAcceptedRequestClicked(notification) }
            itemView.notification_item_user_decline.setOnClickListener { notificationClickListener.onDeclinedRequestClicked(notification) }
        }

        private fun calculateTimeSinceNotif(receivedAt: Long): String{
            val c: Calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
            c.timeInMillis = System.currentTimeMillis() + 5000 - receivedAt
            val mYear: Int = c.get(Calendar.YEAR) - 1970
            val mMonth: Int = c.get(Calendar.MONTH)
            val mDay: Int = c.get(Calendar.DAY_OF_MONTH) - 1
            val mHours: Int = c.get(Calendar.HOUR)
            val mMin : Int = c.get(Calendar.MINUTE)

            val timeSince: String
            if(mYear <= 0){
                if(mMonth <= 0){
                    if(mDay <=0){
                        if(mHours <= 0){
                            if(mMin <= 0){
                                timeSince = "few seconds ago"
                            } else {
                                if(mMin > 1) timeSince = "$mMin minutes"
                                else timeSince = "$mMin minute"
                            }
                        } else {
                            if(mHours > 1) timeSince = "$mHours hours"
                            else timeSince = "$mHours hour"
                        }
                    } else {
                        if(mDay > 1) timeSince = "$mDay days"
                        else timeSince = "$mDay day"
                    }
                } else {
                    if(mMonth > 1) timeSince = "$mMonth months"
                    else timeSince = "$mMonth month"
                }
            } else {
                if(mYear > 1) timeSince = "$mYear years"
                else timeSince = "$mYear year"
            }

            return timeSince
        }


    }
}