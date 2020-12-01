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
import com.timenoteco.timenote.common.bytesEqualTo
import com.timenoteco.timenote.common.pixelsEqualTo
import com.timenoteco.timenote.model.TimenoteInfoDTO
import com.timenoteco.timenote.model.UserInfoDTO
import kotlinx.android.synthetic.main.item_user.view.givenName
import kotlinx.android.synthetic.main.item_user.view.name_user
import kotlinx.android.synthetic.main.item_user.view.user_imageview
import kotlinx.android.synthetic.main.item_user_send.view.*

class UsersShareWithPagingAdapter(diffCallback: DiffUtil.ItemCallback<UserInfoDTO>,
                                  val searchPeopleListener: SearchPeopleListener,
                                  val addToSend: AddToSend)
    : PagingDataAdapter<UserInfoDTO, UsersShareWithPagingAdapter.UserViewHolder>(diffCallback){

    interface SearchPeopleListener{
        fun onSearchClicked(userInfoDTO: UserInfoDTO)
    }

    interface AddToSend{
        fun onAdd(userInfoDTO: UserInfoDTO)
        fun onRemove(userInfoDTO: UserInfoDTO)
    }

    class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindUser(
            userInfoDTO: UserInfoDTO?,
            searchPeopleListener: SearchPeopleListener,
            addToSend: AddToSend
        ) {

                Glide
                    .with(itemView)
                    .load(userInfoDTO?.picture)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.circle_pic)
                    .into(itemView.user_imageview)

            itemView.name_user.text = userInfoDTO?.userName
            if(userInfoDTO?.givenName.isNullOrBlank()) itemView.givenName.visibility = View.GONE else {
                itemView.givenName.visibility = View.VISIBLE
                itemView.givenName.text = userInfoDTO?.givenName
            }
            itemView.user_imageview.setOnClickListener { searchPeopleListener.onSearchClicked(userInfoDTO!!) }
            itemView.name_user.setOnClickListener { searchPeopleListener.onSearchClicked(userInfoDTO!!) }
            itemView.givenName.setOnClickListener { searchPeopleListener.onSearchClicked(userInfoDTO!!) }
            itemView.item_user_send.setOnClickListener {
                if(itemView.item_user_send.drawable.bytesEqualTo(itemView.context.resources.getDrawable(R.drawable.ic_add_circle_yellow_send)) && itemView.item_user_send.drawable.pixelsEqualTo(itemView.context.resources.getDrawable(R.drawable.ic_add_circle_yellow_send))){
                    addToSend.onAdd(userInfoDTO!!)
                    itemView.item_user_send.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.ic_baseline_remove_send))
                } else {
                    addToSend.onRemove(userInfoDTO!!)
                    itemView.item_user_send.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.ic_add_circle_yellow_send))
                }
            }
        }

    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bindUser(getItem(position), searchPeopleListener, addToSend)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder =
        UserViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_user_send, parent, false))

    object UserComparator: DiffUtil.ItemCallback<UserInfoDTO>(){
        override fun areItemsTheSame(oldItem: UserInfoDTO, newItem: UserInfoDTO): Boolean {
            return oldItem.createdAt == newItem.createdAt
        }

        override fun areContentsTheSame(oldItem: UserInfoDTO, newItem: UserInfoDTO): Boolean {
            return oldItem == newItem
        }


    }

}