package com.timenoteco.timenote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.timenoteco.timenote.R
import com.timenoteco.timenote.model.TimenoteInfoDTO
import com.timenoteco.timenote.model.UserInfoDTO
import kotlinx.android.synthetic.main.item_user.view.*

class UsersPagingAdapter(diffCallback: DiffUtil.ItemCallback<UserInfoDTO>, val timenoteInfoDTO: TimenoteInfoDTO?,
                         val searchPeopleListener: SearchPeopleListener): PagingDataAdapter<UserInfoDTO, UsersPagingAdapter.UserViewHolder>(diffCallback){

    interface SearchPeopleListener{
        fun onSearchClicked(userInfoDTO: UserInfoDTO, timenoteInfoDTO: TimenoteInfoDTO?)
    }

    class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindUser(
            item: UserInfoDTO?,
            searchPeopleListener: SearchPeopleListener,
            timenoteInfoDTO: TimenoteInfoDTO?
        ) {

                Glide
                    .with(itemView)
                    .load(item?.picture)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.circle_pic)
                    .into(itemView.user_imageview)

            itemView.name_user.text = item?.userName
            //if(item?.givenName == null) itemView.givenName.visibility = View.GONE else
            itemView.givenName.text = item?.givenName
            itemView.setOnClickListener { searchPeopleListener.onSearchClicked(item!!, timenoteInfoDTO) }
        }

    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bindUser(getItem(position), searchPeopleListener, timenoteInfoDTO)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder =
        UserViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false))

    object UserComparator: DiffUtil.ItemCallback<UserInfoDTO>(){
        override fun areItemsTheSame(oldItem: UserInfoDTO, newItem: UserInfoDTO): Boolean {
            return oldItem.createdAt == newItem.createdAt
        }

        override fun areContentsTheSame(oldItem: UserInfoDTO, newItem: UserInfoDTO): Boolean {
            return oldItem == newItem
        }


    }

}