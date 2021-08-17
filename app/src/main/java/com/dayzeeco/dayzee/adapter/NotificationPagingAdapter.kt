package com.dayzeeco.dayzee.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.model.CommentInfoDTO
import com.dayzeeco.dayzee.model.NotificationInfoDTO

class NotificationPagingAdapter(diffCallback: DiffUtil.ItemCallback<NotificationInfoDTO>, private val notificationClickListener: NotificationAdapter.NotificationClickListener): PagingDataAdapter<NotificationInfoDTO, NotificationAdapter.NotificationViewHolder>(diffCallback) {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(
        holder: NotificationAdapter.NotificationViewHolder,
        position: Int
    ) {
        holder.bindNotification(getItem(position)!!, notificationClickListener)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationAdapter.NotificationViewHolder = NotificationAdapter.NotificationViewHolder(LayoutInflater.from(parent.context).inflate(
        R.layout.item_notification, parent, false))
}

object NotificationComparator : DiffUtil.ItemCallback<NotificationInfoDTO>(){

    override fun areItemsTheSame(oldItem: NotificationInfoDTO, newItem: NotificationInfoDTO): Boolean {
        return oldItem.createdAt == newItem.createdAt
    }

    override fun areContentsTheSame(oldItem: NotificationInfoDTO, newItem: NotificationInfoDTO): Boolean {
        return oldItem == newItem
    }

}