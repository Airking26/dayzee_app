package com.dayzeeco.dayzee.video

import android.view.View
import com.bumptech.glide.Glide
import com.dayzeeco.dayzee.video.VideoAdapter.VideoViewHolder
import com.dayzeeco.dayzee.video.video_player_manager.manager.VideoPlayerManager
import com.dayzeeco.dayzee.video.video_player_manager.meta.MetaData
import com.dayzeeco.dayzee.video.video_player_manager.ui.VideoPlayerView


class UrlVideoItem(
    private val mDirectUrl: String?,
    videoPlayerManager: VideoPlayerManager<MetaData>,
    imageResource: String
) :
    BaseVideoItem(videoPlayerManager) {
    private val mImageResource: String = imageResource

    override fun update(
        position: Int,
        view: VideoViewHolder?,
        videoPlayerManager: VideoPlayerManager<*>?
    ) {
        view!!.mCover.visibility = View.VISIBLE
        Glide.with(view.itemView.context).load(mImageResource).into(view.mCover)
    }

    override fun playNewVideo(
        currentItemMetaData: MetaData,
        player: VideoPlayerView,
        videoPlayerManager: VideoPlayerManager<MetaData>
    ) {
        videoPlayerManager.playNewVideo(currentItemMetaData, player, mDirectUrl)
    }

    override fun stopPlayback(videoPlayerManager: VideoPlayerManager<*>) {
        videoPlayerManager.stopAnyPlayback()
    }

    fun isVideo(): Boolean{
        return !mDirectUrl.isNullOrBlank()
    }

    fun getUrl(): String{
        return mImageResource
    }

}