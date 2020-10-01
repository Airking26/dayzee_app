package com.timenoteco.timenote.adapter

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.timenoteco.timenote.R
import com.timenoteco.timenote.common.Utils
import com.timenoteco.timenote.model.Notification
import kotlinx.android.synthetic.main.item_comment.view.*
import kotlinx.android.synthetic.main.item_notification.view.*

class NotificationAdapter(private val notifications: MutableList<Notification>): RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder =
        NotificationViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false))

    override fun getItemCount(): Int = notifications.size

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) = holder.bindNotification(notifications[position])

    class NotificationViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        @RequiresApi(Build.VERSION_CODES.M)
        fun bindNotification(notification: Notification) {

            if(!notification.read){
                itemView.setBackgroundColor(Color.parseColor("#20aaaaaa"))
            } else {
                itemView.setBackgroundColor(Color.parseColor("#ffffff"))
            }

            Glide
                .with(itemView)
                .load("https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260")
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.circle_pic)
                .into(itemView.notification_user_pic_imageview)

            itemView.notification_annoucement.text = notification.message
            itemView.notification_time.text = "17 minutes"
        }

    }
}