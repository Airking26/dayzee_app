package com.dayzeeco.dayzee.video

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.common.position
import com.dayzeeco.dayzee.video.video_player_manager.manager.VideoPlayerManager
import com.dayzeeco.dayzee.video.video_player_manager.meta.MetaData
import kotlinx.android.synthetic.main.item_video.view.*

class VideoAdapter(private val videoPlayerManager: VideoPlayerManager<MetaData>, private val videos : List<BaseVideoItem>, private val context: Context): RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {


    class VideoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val mPlayer = itemView.player
        val mCover = itemView.cover

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val item = videos[viewType]
        val resultView = item.createView(parent, context.resources.displayMetrics.widthPixels)
        return VideoViewHolder(resultView)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val item = videos[position]
        item.update(position, holder, videoPlayerManager)
    }

    override fun getItemCount(): Int = videos.size

}