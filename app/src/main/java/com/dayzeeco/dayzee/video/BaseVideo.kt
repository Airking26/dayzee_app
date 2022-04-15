package com.dayzeeco.dayzee.video

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.video.VideoAdapter.VideoViewHolder
import com.dayzeeco.dayzee.video.video_player_manager.manager.VideoItem
import com.dayzeeco.dayzee.video.video_player_manager.manager.VideoPlayerManager
import com.dayzeeco.dayzee.video.video_player_manager.meta.CurrentItemMetaData
import com.dayzeeco.dayzee.video.video_player_manager.meta.MetaData
import com.dayzeeco.dayzee.video.video_player_manager.ui.MediaPlayerWrapper
import com.dayzeeco.dayzee.video.video_player_manager.utils.Logger
import com.dayzeeco.dayzee.video.visibility_utils.items.ListItem
import com.moralis.web3.Moralis


abstract class BaseVideoItem protected constructor(private val mVideoPlayerManager: VideoPlayerManager<MetaData>) :
    VideoItem, ListItem {

    private val mCurrentViewRect: Rect = Rect()
    abstract fun update(
        position: Int,
        view: VideoViewHolder?,
        videoPlayerManager: VideoPlayerManager<*>?
    )

    /**
     * When this item becomes active we start playback on the video in this item
     */
    override fun setActive(newActiveView: View, newActiveViewPosition: Int) {
        if (newActiveView.getTag() is VideoViewHolder) {
            val viewHolder = newActiveView.getTag() as VideoViewHolder
            playNewVideo(
                CurrentItemMetaData(newActiveViewPosition, newActiveView),
                viewHolder.mPlayer,
                mVideoPlayerManager
            )
        }
    }

    /**
     * When this item becomes inactive we stop playback on the video in this item.
     */
    override fun deactivate(currentView: View?, position: Int) {
        stopPlayback(mVideoPlayerManager)
    }

    fun createView(parent: ViewGroup, screenWidth: Int): View {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
        //val layoutParams: ViewGroup.LayoutParams = view.layoutParams
        //layoutParams.height = screenWidth
        val videoViewHolder = VideoViewHolder(view)
        view.tag = videoViewHolder
        videoViewHolder.mPlayer.addMediaPlayerListener(object :
            MediaPlayerWrapper.MainThreadMediaPlayerListener {
            override fun onVideoSizeChangedMainThread(width: Int, height: Int) {}
            override fun onVideoPreparedMainThread() {
                videoViewHolder.mCover.visibility = View.INVISIBLE
            }

            override fun onVideoCompletionMainThread() {}
            override fun onErrorMainThread(what: Int, extra: Int) {}
            override fun onBufferingUpdateMainThread(percent: Int) {}
            override fun onVideoStoppedMainThread() {
                videoViewHolder.mCover.visibility = View.VISIBLE
            }
        })
        return view
    }

    /**
     * This method calculates visibility percentage of currentView.
     * This method works correctly when currentView is smaller then it's enclosure.
     * @param currentView - view which visibility should be calculated
     * @return currentView visibility percents
     */
    override fun getVisibilityPercents(currentView: View): Int {
        if (SHOW_LOGS) Logger.v(
            TAG,
            ">> getVisibilityPercents currentView $currentView"
        )
        var percents = 100
        currentView.getLocalVisibleRect(mCurrentViewRect)
        if (SHOW_LOGS) Logger.v(
            TAG,
            "getVisibilityPercents mCurrentViewRect top " + mCurrentViewRect.top.toString() + ", left " + mCurrentViewRect.left.toString() + ", bottom " + mCurrentViewRect.bottom.toString() + ", right " + mCurrentViewRect.right
        )
        val height: Int = currentView.height
        if (SHOW_LOGS) Logger.v(
            TAG,
            "getVisibilityPercents height $height"
        )
        if (viewIsPartiallyHiddenTop()) {
            // view is partially hidden behind the top edge
            percents = (height - mCurrentViewRect.top) * 100 / height
        } else if (viewIsPartiallyHiddenBottom(height)) {
            percents = mCurrentViewRect.bottom * 100 / height
        }
        setVisibilityPercentsText(currentView, percents)
        if (SHOW_LOGS) Logger.v(
            TAG,
            "<< getVisibilityPercents, percents $percents"
        )
        return percents
    }

    private fun setVisibilityPercentsText(currentView: View, percents: Int) {
        if (SHOW_LOGS) Logger.v(
            TAG,
            "setVisibilityPercentsText percents $percents"
        )
        //val videoViewHolder = currentView.getTag() as VideoViewHolder
        //val percentsText = "Visibility percents: $percents"
        //videoViewHolder.mVisibilityPercents.setText(percentsText)
    }

    private fun viewIsPartiallyHiddenBottom(height: Int): Boolean {
        return mCurrentViewRect.bottom in 1 until height
    }

    private fun viewIsPartiallyHiddenTop(): Boolean {
        return mCurrentViewRect.top > 0
    }

    companion object {
        private const val SHOW_LOGS = false
        private val TAG = BaseVideoItem::class.java.simpleName
    }
}