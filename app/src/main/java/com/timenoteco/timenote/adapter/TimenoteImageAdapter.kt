package com.timenoteco.timenote.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.timenoteco.timenote.R
import kotlinx.android.synthetic.main.timenote_image_view_holder.view.*
import kotlinx.android.synthetic.main.timenote_view_image.*

class TimenoteImageAdapter(val pictures: List<String>) : RecyclerView.Adapter<TimenoteImageAdapter.ItemTimenoteImageViewHolder>() {


    class ItemTimenoteImageViewHolder(itemview: View): RecyclerView.ViewHolder(itemview) {
        fun bindImage(pic: String) {
            Glide.with(itemView.context)
                .load(Uri.parse(pic))
                .thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(itemView.timenote_image_id)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemTimenoteImageViewHolder {
        return ItemTimenoteImageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.timenote_image_view_holder, parent, false))
    }

    override fun getItemCount(): Int {
        return pictures.size
    }

    override fun onBindViewHolder(holder: ItemTimenoteImageViewHolder, position: Int) {
        holder.bindImage(pictures[position])
    }

}