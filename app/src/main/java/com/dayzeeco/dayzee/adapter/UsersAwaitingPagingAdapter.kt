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
import com.dayzeeco.dayzee.model.TimenoteInfoDTO
import com.dayzeeco.dayzee.model.UserInfoDTO
import kotlinx.android.synthetic.main.item_user.view.givenName
import kotlinx.android.synthetic.main.item_user.view.name_user
import kotlinx.android.synthetic.main.item_user.view.user_imageview
import kotlinx.android.synthetic.main.item_user_awaiting.view.*

class UsersAwaitingPagingAdapter(diffCallback: DiffUtil.ItemCallback<UserInfoDTO>,
                                 val timenoteInfoDTO: TimenoteInfoDTO?,
                                 val searchPeopleListener: SearchPeopleListener,
                                 val acceptDecline: AcceptDecline,
                                 private val isTagged: Boolean,
                                 private val utils: Utils
)
    : PagingDataAdapter<UserInfoDTO, UsersAwaitingPagingAdapter.UserViewHolder>(diffCallback){

    interface SearchPeopleListener{
        fun onSearchClicked(userInfoDTO: UserInfoDTO, isTagged: Boolean)
    }

    interface AcceptDecline{
        fun onAccept(userInfoDTO: UserInfoDTO, position: Int)
        fun onDecline(userInfoDTO: UserInfoDTO, position: Int)
    }

    class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindUser(
            userInfoDTO: UserInfoDTO?,
            searchPeopleListener: SearchPeopleListener,
            acceptDecline: AcceptDecline,
            position: Int,
            isTagged: Boolean,
            utils: Utils
        ) {

            if (userInfoDTO?.picture.isNullOrBlank()){
                itemView.user_imageview.setImageDrawable(utils.determineLetterLogo(userInfoDTO?.userName!!, itemView.context))
            } else Glide
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
            itemView.user_imageview.setOnClickListener { searchPeopleListener.onSearchClicked(userInfoDTO!!, isTagged) }
            itemView.name_user.setOnClickListener { searchPeopleListener.onSearchClicked(userInfoDTO!!, isTagged) }
            itemView.givenName.setOnClickListener { searchPeopleListener.onSearchClicked(userInfoDTO!!, isTagged) }
            itemView.item_user_accept.setOnClickListener { acceptDecline.onAccept(userInfoDTO!!, position) }
            itemView.item_user_decline.setOnClickListener { acceptDecline.onDecline(userInfoDTO!!, position) }
        }

    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bindUser(getItem(position), searchPeopleListener, acceptDecline, position, isTagged, utils)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder =
        UserViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_user_awaiting, parent, false))

    object UserComparator: DiffUtil.ItemCallback<UserInfoDTO>(){
        override fun areItemsTheSame(oldItem: UserInfoDTO, newItem: UserInfoDTO): Boolean {
            return oldItem.createdAt == newItem.createdAt
        }

        override fun areContentsTheSame(oldItem: UserInfoDTO, newItem: UserInfoDTO): Boolean {
            return oldItem == newItem
        }


    }

}