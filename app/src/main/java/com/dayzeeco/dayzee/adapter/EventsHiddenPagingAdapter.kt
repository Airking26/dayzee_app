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
import kotlinx.android.synthetic.main.item_event_hidden.view.*
import kotlinx.android.synthetic.main.item_user.view.givenName
import kotlinx.android.synthetic.main.item_user.view.name_user
import kotlinx.android.synthetic.main.item_user.view.user_imageview
import kotlinx.android.synthetic.main.item_user_awaiting.view.*
import kotlinx.android.synthetic.main.item_user_hidden.view.*
import kotlinx.android.synthetic.main.item_user_hidden.view.givenName_hidden

class EventsHiddenPagingAdapter(diffCallback: DiffUtil.ItemCallback<TimenoteInfoDTO>,
                                val searchPeopleListener: GoToEvent,
                                val hideUnhide: HideUnhideEvent,
                                private val isTagged: Boolean
)
    : PagingDataAdapter<TimenoteInfoDTO, EventsHiddenPagingAdapter.UserViewHolder>(diffCallback){

    interface GoToEvent{
        fun onEventClicked(timenote: TimenoteInfoDTO, isTagged: Boolean)
    }

    interface HideUnhideEvent{
        fun onSwitch(timenote: TimenoteInfoDTO, unHide: Boolean)
    }

    class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindUser(
            timenote: TimenoteInfoDTO?,
            searchPeopleListener: GoToEvent,
            acceptDecline: HideUnhideEvent,
            isTagged: Boolean
        ) {

                Glide
                    .with(itemView)
                    .load(timenote?.pictures?.get(0))
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.circle_pic)
                    .into(itemView.event_hidden_imageview)

            itemView.name_hidden_event.text = timenote?.title
            itemView.givenName_event_hidden.text = timenote?.startingAt
            itemView.event_hidden_imageview.setOnClickListener { searchPeopleListener.onEventClicked(timenote!!, isTagged) }
            itemView.name_hidden_event.setOnClickListener { searchPeopleListener.onEventClicked(timenote!!, isTagged) }
            itemView.givenName_event_hidden.setOnClickListener { searchPeopleListener.onEventClicked(timenote!!, isTagged) }
            itemView.item_event_hidden_on_off.setOnClickListener {
                if(itemView.item_event_hidden_on_off.drawable.bytesEqualTo(itemView.context.resources.getDrawable(R.drawable.ic_baseline_visibility_off_24)) && itemView.item_user_hidden_on_off.drawable.pixelsEqualTo(itemView.context.resources.getDrawable(R.drawable.ic_baseline_visibility_off_24))) {
                    itemView.item_event_hidden_on_off.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.ic_baseline_visibility_24))
                    acceptDecline.onSwitch(timenote!!, true)
                } else {
                    itemView.item_event_hidden_on_off.setImageDrawable(itemView.context.resources.getDrawable(R.drawable.ic_baseline_visibility_off_24))
                    acceptDecline.onSwitch(timenote!!, false)
                }

            }
        }

    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bindUser(getItem(position), searchPeopleListener, hideUnhide, isTagged)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder =
        UserViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_event_hidden, parent, false))

    object TimenoteComparator: DiffUtil.ItemCallback<TimenoteInfoDTO>(){
        override fun areItemsTheSame(oldItem: TimenoteInfoDTO, newItem: TimenoteInfoDTO): Boolean {
            return oldItem.createdAt == newItem.createdAt
        }

        override fun areContentsTheSame(oldItem: TimenoteInfoDTO, newItem: TimenoteInfoDTO): Boolean {
            return oldItem == newItem
        }


    }

}