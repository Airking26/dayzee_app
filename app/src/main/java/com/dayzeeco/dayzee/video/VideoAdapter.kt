package com.dayzeeco.dayzee.video

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.common.position
import com.dayzeeco.dayzee.video.video_player_manager.manager.VideoPlayerManager
import com.dayzeeco.dayzee.video.video_player_manager.meta.MetaData
import kotlinx.android.synthetic.main.item_test.view.*
import kotlinx.android.synthetic.main.item_video.view.*

class VideoAdapter(private val videoPlayerManager: VideoPlayerManager<MetaData>, private val videos : List<UrlVideoItem>, private val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    class VideoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val mPlayer = itemView.player
        val mCover = itemView.cover

    }

    class ImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val image = itemView.test

        fun bindImage(urlVideoItem: UrlVideoItem) {
            image.setImageURI(Uri.parse(urlVideoItem.getUrl()))
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val item = videos[viewType]
            val resultView = item.createView(parent, context.resources.displayMetrics.widthPixels)
            VideoViewHolder(resultView)
        } else
            ImageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_test, parent, false))
    }

    override fun getItemViewType(position: Int): Int {
        return if (videos[position].isVideo()) 0
        else 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType){
            0 -> {
                val item = videos[position]
                item.update(position, holder as VideoViewHolder, videoPlayerManager)
            }
            1 -> (holder as ImageViewHolder).bindImage(videos[position])
        }

    }

    override fun getItemCount(): Int = videos.size

}