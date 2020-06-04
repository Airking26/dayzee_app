package com.timenoteco.timenote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.timenoteco.timenote.R
import com.timenoteco.timenote.model.Timenote
import kotlinx.android.synthetic.main.fragment_signup.view.*
import kotlinx.android.synthetic.main.item_profile_calendar.view.*
import kotlinx.android.synthetic.main.item_timenote.view.*

class ItemTimenoteRegularAdapter(val timenotes: List<Timenote>): RecyclerView.Adapter<ItemTimenoteRegularAdapter.TimenoteViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimenoteViewHolder =
        TimenoteViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_timenote, parent, false))

    override fun getItemCount(): Int = timenotes.size

    override fun onBindViewHolder(holder: TimenoteViewHolder, position: Int) = holder.bindTimenote(timenotes[position])

    class TimenoteViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bindTimenote(timenote: Timenote) {
            Glide
                .with(itemView)
                .load(timenote.pic_user)
                .apply(RequestOptions.circleCropTransform())
                .into(itemView.timenote_pic_user_imageview)

            Glide
                .with(itemView)
                .load(timenote.pic)
                .centerCrop()
                .into(itemView.timenote_pic_imageview)

            itemView.timenote_username.text = timenote.username
            itemView.timenote_place.text = timenote.place
            itemView.timenote_like_number.text = timenote.nbrLikes
            itemView.timenote_username_desc.text = timenote.desc
            itemView.timenote_see_comments.text = timenote.seeComments

        }

    }

}