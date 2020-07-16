package com.timenoteco.timenote.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.timenoteco.timenote.R
import com.timenoteco.timenote.model.statusTimenote
import kotlinx.android.synthetic.main.timenote_view_image.view.*

class TimenoteViewPagerAdapter(
    var timenote: MutableList<String>?,
    private val hideIcons: Boolean,
    private val itemClickListener: (Int) -> (Unit)
): RecyclerView.Adapter<TimenoteViewPagerAdapter.TimenoteViewPagerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimenoteViewPagerViewHolder =
        TimenoteViewPagerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.timenote_view_image, parent, false))

    override fun getItemCount(): Int = timenote?.size!!

    override fun onBindViewHolder(holder: TimenoteViewPagerViewHolder, position: Int) {
        holder.bind(timenote?.get(position), hideIcons, itemClickListener)
    }


    class TimenoteViewPagerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(
            get: String?,
            hideIcons: Boolean,
            itemClickListener: (Int) -> Unit
        ) {
            Glide.with(itemView)
                .load(Uri.parse(get))
                .into(itemView.create_timenote_pic)

            if(hideIcons){
                itemView.timenote_change_pic.visibility = View.GONE
                itemView.timenote_crop_pic.visibility = View.GONE
                itemView.timenote_delete_pic.visibility = View.GONE
                itemView.timenote_add_pic.visibility = View.GONE
            } else {
                itemView.timenote_change_pic.visibility = View.VISIBLE
                itemView.timenote_crop_pic.visibility = View.VISIBLE
                itemView.timenote_delete_pic.visibility = View.VISIBLE
                itemView.timenote_add_pic.visibility = View.VISIBLE
            }

            itemView.create_timenote_pic.setOnClickListener {
                itemClickListener(adapterPosition)
            }
        }

    }
}