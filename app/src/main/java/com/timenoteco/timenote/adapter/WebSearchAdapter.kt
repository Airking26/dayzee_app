package com.timenoteco.timenote.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.timenoteco.timenote.R
import com.timenoteco.timenote.common.Utils
import kotlinx.android.synthetic.main.item_more_web_search.view.*
import kotlinx.android.synthetic.main.timenote_view_image.view.*

class WebSearchAdapter(
    val images: MutableList<String>,
    val imageChoosedListener: ImageChoosedListener,
    val moreImagesClicked: MoreImagesClicked,
    val query: String,
    val dialog: MaterialDialog
): RecyclerView.Adapter<WebSearchAdapter.WebSearchHolder>() {

    interface ImageChoosedListener{
        fun onImageSelectedFromWeb(bitmap: String, dialog: MaterialDialog)
    }

    interface MoreImagesClicked{
        fun onMoreImagesClicked(position: Int, query: String)
    }

    fun clear(){
        this.images.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WebSearchHolder {
        val itemView: View = if(viewType == R.layout.item_image_web_search){
            LayoutInflater.from(parent.context).inflate(R.layout.item_image_web_search, parent, false)
        } else {
            LayoutInflater.from(parent.context).inflate(R.layout.item_more_web_search, parent, false)
        }
        return WebSearchHolder(itemView)
    }

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: WebSearchHolder, position: Int) {
        if(position == images.size - 1) holder.bindMore(moreImagesClicked, query, position)
        else holder.bindImages(images[position], imageChoosedListener, dialog)
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == images.size - 1) R.layout.item_more_web_search else R.layout.item_image_web_search
    }

    class WebSearchHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bindImages(bitmap: String, imageChoosedListener: ImageChoosedListener, dialog: MaterialDialog) {

            val circularProgressDrawable = Utils().createPb(itemView.context)

            Glide.with(itemView)
                    .load(Uri.parse(bitmap))
                    .placeholder(circularProgressDrawable)
                    .centerInside()
                    .into(itemView.create_timenote_pic)

                itemView.create_timenote_pic.setOnClickListener{
                    imageChoosedListener.onImageSelectedFromWeb(bitmap, dialog)
                }
        }

        fun bindMore(
            moreImagesClicked: MoreImagesClicked,
            query: String,
            position: Int
        ) {
            itemView.more_web_images.setOnClickListener { moreImagesClicked.onMoreImagesClicked(position, query) }
        }

    }
}