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

class UsersShareWithPagingAdapter(
    diffCallback: DiffUtil.ItemCallback<UserInfoDTO>,
    private val searchPeopleListener: SearchPeopleListener,
    private val addToSend: AddToSend,
    private val organizers: MutableList<String>?,
    private val sendTo: MutableList<String>?,
    private val createGroup: Int?,
    private val isTagged: Boolean,
    private val utils: Utils
)
    : PagingDataAdapter<UserInfoDTO, UsersShareWithPagingAdapter.UserViewHolder>(diffCallback){

    interface SearchPeopleListener{
        fun onSearchClicked(userInfoDTO: UserInfoDTO, isTagged: Boolean)
    }

    interface AddToSend{
        fun onAdd(userInfoDTO: UserInfoDTO, createGroup: Int?)
        fun onRemove(userInfoDTO: UserInfoDTO, createGroup: Int?)
    }

    class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindUser(
            userInfoDTO: UserInfoDTO?,
            searchPeopleListener: SearchPeopleListener,
            addToSend: AddToSend,
            organizers: MutableList<String>?,
            sendTo: MutableList<String>?,
            createGroup: Int?,
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
            if(createGroup != null && createGroup == 2 && organizers != null && organizers.contains(userInfoDTO?.id)) itemView.item_user_send.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.ic_baseline_remove_send))
            else if(sendTo != null && sendTo.contains(userInfoDTO?.id)) itemView.item_user_send.setImageDrawable(itemView.context.getDrawable(R.drawable.ic_baseline_remove_send))
            itemView.user_imageview.setOnClickListener { searchPeopleListener.onSearchClicked(userInfoDTO!!, isTagged) }
            itemView.name_user.setOnClickListener { searchPeopleListener.onSearchClicked(userInfoDTO!!, isTagged) }
            itemView.givenName.setOnClickListener { searchPeopleListener.onSearchClicked(userInfoDTO!!, isTagged) }
            itemView.item_user_send.setOnClickListener {
                if(itemView.item_user_send.drawable.bytesEqualTo(itemView.context.resources.getDrawable(R.drawable.ic_add_circle_yellow_send)) && itemView.item_user_send.drawable.pixelsEqualTo(itemView.context.resources.getDrawable(R.drawable.ic_add_circle_yellow_send))){
                    itemView.user_rl.background = itemView.context.getDrawable(R.drawable.border_selected)
                    addToSend.onAdd(userInfoDTO!!, createGroup)
                    itemView.item_user_send.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.ic_baseline_remove_send))
                } else {
                    itemView.user_rl.background = itemView.context.getDrawable(R.drawable.border_unselected)
                    addToSend.onRemove(userInfoDTO!!, createGroup)
                    itemView.item_user_send.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.ic_add_circle_yellow_send))
                }
            }
        }

    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bindUser(getItem(position), searchPeopleListener, addToSend, organizers, sendTo, createGroup, isTagged, utils)
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