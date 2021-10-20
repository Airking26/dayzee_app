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
import com.dayzeeco.dayzee.common.bytesEqualTo
import com.dayzeeco.dayzee.common.pixelsEqualTo
import com.dayzeeco.dayzee.model.TimenoteInfoDTO
import com.dayzeeco.dayzee.model.UserInfoDTO
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.item_user.view.givenName
import kotlinx.android.synthetic.main.item_user.view.name_user
import kotlinx.android.synthetic.main.item_user.view.user_imageview
import kotlinx.android.synthetic.main.item_user_awaiting.view.*
import kotlinx.android.synthetic.main.item_user_hidden.view.*

class UsersHiddenPagingAdapter(diffCallback: DiffUtil.ItemCallback<UserInfoDTO>,
                               val searchPeopleListener: SearchPeopleListener,
                               val hideUnhide: HideUnhide,
                               private val isTagged: Boolean
)
    : PagingDataAdapter<UserInfoDTO, UsersHiddenPagingAdapter.UserViewHolder>(diffCallback){

    interface SearchPeopleListener{
        fun onSearchClicked(userInfoDTO: UserInfoDTO, isTagged: Boolean)
    }

    interface HideUnhide{
        fun onSwitch(userInfoDTO: UserInfoDTO, unHide: Boolean)
    }

    class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindUser(
            userInfoDTO: UserInfoDTO?,
            searchPeopleListener: SearchPeopleListener,
            acceptDecline: HideUnhide,
            isTagged: Boolean
        ) {

                Glide
                    .with(itemView)
                    .load(userInfoDTO?.picture)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.circle_pic)
                    .into(itemView.user_hidden_imageview)

            itemView.name_hidden_user.text = userInfoDTO?.userName
            if(userInfoDTO?.givenName.isNullOrBlank()) itemView.givenName_hidden.visibility = View.GONE else {
                itemView.givenName_hidden.visibility = View.VISIBLE
                itemView.givenName_hidden.text = userInfoDTO?.givenName
            }
            itemView.user_hidden_imageview.setOnClickListener { searchPeopleListener.onSearchClicked(userInfoDTO!!, isTagged) }
            itemView.name_hidden_user.setOnClickListener { searchPeopleListener.onSearchClicked(userInfoDTO!!, isTagged) }
            itemView.givenName_hidden.setOnClickListener { searchPeopleListener.onSearchClicked(userInfoDTO!!, isTagged) }
            itemView.item_user_hidden_on_off.setOnClickListener {
                if(itemView.item_user_hidden_on_off.drawable.bytesEqualTo(itemView.context.resources.getDrawable(R.drawable.ic_baseline_visibility_off_24)) && itemView.item_user_hidden_on_off.drawable.pixelsEqualTo(itemView.context.resources.getDrawable(R.drawable.ic_baseline_visibility_off_24))) {
                    itemView.item_user_hidden_on_off.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.ic_baseline_visibility_24))
                    acceptDecline.onSwitch(userInfoDTO!!, true)
                } else {
                    itemView.item_user_hidden_on_off.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.ic_baseline_visibility_off_24))
                    acceptDecline.onSwitch(userInfoDTO!!, false)
                }

            }
        }

    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bindUser(getItem(position), searchPeopleListener, hideUnhide, isTagged)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder =
        UserViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_user_hidden, parent, false))

    object UserComparator: DiffUtil.ItemCallback<UserInfoDTO>(){
        override fun areItemsTheSame(oldItem: UserInfoDTO, newItem: UserInfoDTO): Boolean {
            return oldItem.createdAt == newItem.createdAt
        }

        override fun areContentsTheSame(oldItem: UserInfoDTO, newItem: UserInfoDTO): Boolean {
            return oldItem == newItem
        }


    }

}