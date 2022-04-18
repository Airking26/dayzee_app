package com.dayzeeco.dayzee.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.common.Utils
import com.dayzeeco.dayzee.common.bytesEqualTo
import com.dayzeeco.dayzee.common.pixelsEqualTo
import com.dayzeeco.dayzee.model.UserInfoDTO
import kotlinx.android.synthetic.main.item_user.view.givenName
import kotlinx.android.synthetic.main.item_user.view.name_user
import kotlinx.android.synthetic.main.item_user.view.user_imageview
import kotlinx.android.synthetic.main.item_user_send.view.*

class UsersTopPagingAdapter(diffCallback: DiffUtil.ItemCallback<UserInfoDTO>, private val searchPeopleListener: SearchPeopleListener) : PagingDataAdapter<UserInfoDTO, UsersTopPagingAdapter.UserViewHolder>(diffCallback){

    interface SearchPeopleListener{
        fun onSearchClicked(userInfoDTO: UserInfoDTO)
    }

    interface AddToSend{
        fun onAdd(userInfoDTO: UserInfoDTO, createGroup: Int?)
        fun onRemove(userInfoDTO: UserInfoDTO, createGroup: Int?)
    }

    class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindUser(
            userInfoDTO: UserInfoDTO?,
            searchPeopleListener: SearchPeopleListener) {

            if (userInfoDTO?.picture.isNullOrBlank()){
                Utils().determineLetterLogo(userInfoDTO?.userName!!, itemView.context)
            } else {
                if (userInfoDTO?.isPictureNft!!) itemView.user_imageview.vertices = 6
                else itemView.user_imageview.vertices = 0
                Glide
                    .with(itemView)
                    .load(userInfoDTO?.picture)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.circle_pic)
                    .into(itemView.user_imageview)
            }

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
                    itemView.user_rl.background = itemView.context.getDrawable(R.drawable.border_selected)
                    itemView.item_user_send.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.ic_baseline_remove_send))
                } else {
                    itemView.user_rl.background = itemView.context.getDrawable(R.drawable.border_unselected)
                    itemView.item_user_send.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.ic_add_circle_yellow_send))
                }
            }
        }

    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bindUser(getItem(position), searchPeopleListener)
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