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

class UsersPagingAdapter(diffCallback: DiffUtil.ItemCallback<UserInfoDTO>,
                         val timenoteInfoDTO: TimenoteInfoDTO?,
                         private val searchPeopleListener: SearchPeopleListener,
                         private val mine: Boolean?,
                         val followers: Int?
)
    : PagingDataAdapter<UserInfoDTO, UsersPagingAdapter.UserViewHolder>(diffCallback){

    interface SearchPeopleListener{
        fun onSearchClicked(userInfoDTO: UserInfoDTO)
        fun onUnfollow(id: String)
        fun onRemove(id: String)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bindUser(getItem(position), searchPeopleListener, mine, followers)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder =
        UserViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_user,
                parent,
                false
            )
        )

    class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindUser(
            userInfoDTO: UserInfoDTO?,
            searchPeopleListener: SearchPeopleListener,
            mine: Boolean?,
            followers: Int?
        ) {

            if(userInfoDTO?.certified!!) itemView.name_user.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_certification, 0)
            else itemView.name_user.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)

            if(mine != null && mine == true){
                if(followers != null && followers == 0){
                    itemView.user_unfollow.visibility = View.VISIBLE
                    itemView.user_unfollow.setOnClickListener { searchPeopleListener.onUnfollow(userInfoDTO.id!!) }
                } else {
                    itemView.user_remove.visibility = View.VISIBLE
                    itemView.user_remove.setOnClickListener { searchPeopleListener.onRemove(userInfoDTO.id!!) }
                }
            }

            Glide
                .with(itemView)
                .load(userInfoDTO.picture)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.circle_pic)
                .into(itemView.user_imageview)

            itemView.name_user.text = userInfoDTO.userName
            if(userInfoDTO.givenName.isNullOrBlank()) itemView.givenName.visibility = View.GONE else {
                itemView.givenName.visibility = View.VISIBLE
                itemView.givenName.text = userInfoDTO.givenName
            }
            itemView.setOnClickListener { searchPeopleListener.onSearchClicked(userInfoDTO) }
        }

    }

    object UserComparator: DiffUtil.ItemCallback<UserInfoDTO>(){
        override fun areItemsTheSame(oldItem: UserInfoDTO, newItem: UserInfoDTO): Boolean {
            return oldItem.createdAt == newItem.createdAt
        }

        override fun areContentsTheSame(oldItem: UserInfoDTO, newItem: UserInfoDTO): Boolean {
            return oldItem == newItem
        }


    }

}