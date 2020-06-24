package com.timenoteco.timenote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.timenoteco.timenote.R
import com.timenoteco.timenote.model.Timenote
import kotlinx.android.synthetic.main.item_timenote.view.*

class ProfileFilterAdapter(val timenotes: List<Timenote>): RecyclerView.Adapter<ProfileFilterAdapter.ProfileFilterHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileFilterHolder =
        ProfileFilterHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_timenote, parent, false))

    override fun getItemCount(): Int = timenotes.size


    override fun onBindViewHolder(holder: ProfileFilterHolder, position: Int) {
        holder.bindTimenotesAndChips(timenotes[position])
    }

    class ProfileFilterHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bindTimenotesAndChips(timenote: Timenote) {
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
            itemView.timenote_username_desc.text = timenote.desc
            itemView.timenote_title.text = timenote.title
            itemView.timenote_year.text = timenote.year
            itemView.timenote_day_month.text = timenote.month
            itemView.timenote_time.text = timenote.date
            itemView.timenote_day_month.setOnClickListener {
                itemView.separator_1.visibility = View.GONE
                itemView.separator_2.visibility = View.GONE
                itemView.timenote_day_month.visibility = View.GONE
                itemView.timenote_time.visibility = View.GONE
                itemView.timenote_year.visibility = View.GONE
                itemView.timerProgramCountdown.visibility = View.VISIBLE
                itemView.timerProgramCountdown.startCountDown(99999999)
            }
        }

    }
}