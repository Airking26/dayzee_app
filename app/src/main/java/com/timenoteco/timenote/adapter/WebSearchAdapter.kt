package com.timenoteco.timenote.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.timenoteco.timenote.R
import kotlinx.android.synthetic.main.timenote_view_image.view.*

class WebSearchAdapter(val images: List<String>): RecyclerView.Adapter<WebSearchAdapter.WebSearchHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WebSearchHolder =
        WebSearchHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_image_web_search, parent, false))

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: WebSearchHolder, position: Int) {
        holder.bindImages(images[position])
    }

    class WebSearchHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bindImages(s: String) {
            if(s.isNotEmpty() && s.isNotBlank()) {
                Glide.with(itemView)
                    .load(s)
                    .centerInside()
                    .into(itemView.create_timenote_pic)
            }
        }

    }
}