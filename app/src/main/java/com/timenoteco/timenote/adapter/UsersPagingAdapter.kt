package com.timenoteco.timenote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.timenoteco.timenote.R
import com.timenoteco.timenote.model.UserResponse
import kotlinx.android.synthetic.main.item_timenote.view.*
import kotlinx.android.synthetic.main.item_user.view.*

class UsersPagingAdapter(diffCallback: DiffUtil.ItemCallback<UserResponse>): PagingDataAdapter<UserResponse, UsersPagingAdapter.UserViewHolder>(diffCallback){

    class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindUser(item: UserResponse?) {
            Glide
                .with(itemView)
                .load(item?.email)
                .apply(RequestOptions.circleCropTransform())
                .into(itemView.user_imageview)

            itemView.name_user.text = item?.givenName
        }

    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bindUser(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder =
        UserViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false))

    object UserComparator: DiffUtil.ItemCallback<UserResponse>(){
        override fun areItemsTheSame(oldItem: UserResponse, newItem: UserResponse): Boolean {
            return oldItem.createdAt == newItem.createdAt
        }

        override fun areContentsTheSame(oldItem: UserResponse, newItem: UserResponse): Boolean {
            return oldItem == newItem
        }


    }

}