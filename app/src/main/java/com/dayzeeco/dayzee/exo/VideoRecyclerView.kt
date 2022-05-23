package com.dayzeeco.dayzee.exo

import android.content.Context
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.*
import android.view.View.OnClickListener
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.Target
import com.dayzeeco.dayzee.CustomApplicationClass
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.adapter.TimenotePagingAdapter
import com.dayzeeco.dayzee.adapter.TimenoteVideoViewHolder
import com.dayzeeco.dayzee.model.TimenoteInfoDTO
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.*
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.item_timenote_root_video.view.*


class VideoPlayerRecyclerView : RecyclerView {
    private enum class VolumeState {
        ON, OFF
    }

    // ui
    private var viewHolderParent: View? = null
    private var imageView: ImageView? = null
    private lateinit var frameLayout: FrameLayout
    private lateinit var videoSurfaceView: PlayerView
    private var videoPlayer: SimpleExoPlayer? = null

    // vars
    private var videoSurfaceDefaultHeight = 0
    private var screenDefaultHeight = 0
    private var playPosition = -1
    private var isVideoViewAdded = false
    private var mediaObjects: List<TimenoteInfoDTO>? = null


    // controlling playback state
    private var volumeState: VolumeState? = null

    constructor(@NonNull context: Context) : super(context) {
        init(context)
    }

    constructor(@NonNull context: Context, @Nullable attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    private fun init(context: Context) {
        val display: Display = (getContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val point = Point()
        display.getSize(point)
        videoSurfaceDefaultHeight = point.x
        screenDefaultHeight = point.y
        videoSurfaceView = PlayerView(context)
        videoSurfaceView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH

        // 2. Create the player
        videoPlayer = SimpleExoPlayer.Builder(context).build()
        // Bind the player to the view.
        videoSurfaceView.useController = false
        videoSurfaceView.player = videoPlayer
        setVolumeControl(VolumeState.ON)

        addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == SCROLL_STATE_IDLE) {
                    if(imageView != null) imageView!!.visibility = View.VISIBLE
                    // There's a special case when the end of the list has been reached.
                    // Need to handle that with this bit of logic
                    if (!recyclerView.canScrollVertically(1)) {
                        playVideo(true)
                    } else {
                        playVideo(false)
                    }
                }
            }

        })

        addOnChildAttachStateChangeListener(object : OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {
            }
            override fun onChildViewDetachedFromWindow(view: View) {
                if (viewHolderParent != null && viewHolderParent!! == view) {
                    resetVideoView()
                }
            }
        })

        videoPlayer!!.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                    }
                    Player.STATE_ENDED -> {
                        videoPlayer!!.seekTo(0)
                    }
                    Player.STATE_IDLE -> {}
                    Player.STATE_READY -> {
                        if (!isVideoViewAdded) {
                            addVideoView()
                        }
                    }
                    else -> {}
                }
            }
        })
    }

    fun playVideo(isEndOfList: Boolean) {
        val targetPosition: Int
        if (!isEndOfList) {
            val startPosition: Int = (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            var endPosition: Int = (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()

            // if there is more than 2 list-items on the screen, set the difference to be 1
            if (endPosition - startPosition > 1) endPosition = startPosition + 1

            // something is wrong. return.
            if (startPosition < 0 || endPosition < 0) return

            // if there is more than 1 list-item on the screen
            targetPosition = if (startPosition != endPosition) {
                val startPositionVideoHeight = getVisibleVideoSurfaceHeight(startPosition)
                val endPositionVideoHeight = getVisibleVideoSurfaceHeight(endPosition)
                if (startPositionVideoHeight > endPositionVideoHeight) startPosition else endPosition
            } else startPosition

        } else targetPosition = mediaObjects?.size!! - 1

        // video is already playing so return
        if (targetPosition == playPosition) {
            return
        }

        // set the position of the list-item that is to be played
        playPosition = targetPosition

        // remove any old surface views from previously playing videos
        videoSurfaceView.visibility = INVISIBLE
        removeVideoView(videoSurfaceView)
        val currentPosition: Int = targetPosition - (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        val child: View = getChildAt(currentPosition) ?: return
        val holder: TimenoteVideoViewHolder? = child.tag as? TimenoteVideoViewHolder
        if (holder == null) {
            playPosition = -1
            return
        }
        viewHolderParent = holder.itemView
        imageView = holder.itemView.thumbnail
        frameLayout = holder.itemView.findViewById(R.id.media_container)
        videoSurfaceView.player = videoPlayer
        viewHolderParent!!.setOnClickListener(videoViewClickListener)
        val mediaUrl: String? = mediaObjects?.get(targetPosition)?.video ?: "https://file-examples.com/storage/fe2e5158196283b07994c75/2017/04/file_example_MP4_480_1_5MG.mp4"
        if (mediaUrl != null) {
            val urlCached = CustomApplicationClass.getProxy(holder.itemView.context).getProxyUrl(mediaUrl)
            val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
                holder.itemView.context, Util.getUserAgent(holder.itemView.context, "VideoPlayer")
            )
            val mediaSource: MediaSource =
                ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(
                    Uri.parse(urlCached)
                )
            videoPlayer!!.prepare(mediaSource)
            videoPlayer!!.playWhenReady = true
        }
    }

    private val videoViewClickListener: OnClickListener = OnClickListener { toggleVolume() }

    /**
     * Returns the visible region of the video surface on the screen.
     * if some is cut off, it will return less than the @videoSurfaceDefaultHeight
     * @param playPosition
     * @return
     */
    private fun getVisibleVideoSurfaceHeight(playPosition: Int): Int {
        val at: Int = playPosition - (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        val child: View = getChildAt(at) ?: return 0
        val location = IntArray(2)
        child.getLocationInWindow(location)
        return if (location[1] < 0) {
            location[1] + videoSurfaceDefaultHeight
        } else {
            screenDefaultHeight - location[1]
        }
    }

    // Remove the old player
    private fun removeVideoView(videoView: PlayerView?) {
        val parent: ViewParent = videoView?.parent ?: return
        val group : ViewGroup = videoView.parent as ViewGroup
        val index: Int = group.indexOfChild(videoView)
        if (index >= 0) {
            group.removeViewAt(index)
            isVideoViewAdded = false
            viewHolderParent!!.setOnClickListener(null)
        }
    }

    private fun addVideoView() {
        frameLayout.addView(videoSurfaceView)
        isVideoViewAdded = true
        videoSurfaceView.requestFocus()
        videoSurfaceView.visibility = VISIBLE
        videoSurfaceView.alpha = 1f
        imageView!!.visibility = View.INVISIBLE
    }

    private fun resetVideoView() {
        if (isVideoViewAdded) {
            removeVideoView(videoSurfaceView)
            playPosition = -1
            videoSurfaceView.visibility = INVISIBLE
            imageView!!.visibility = View.VISIBLE
        }
    }

    fun releasePlayer() {
        if (videoPlayer != null) {
            videoPlayer!!.release()
            videoPlayer = null
        }
        viewHolderParent = null
    }

    private fun toggleVolume() {
        if (videoPlayer != null) {
            if (volumeState == VolumeState.OFF) {
                setVolumeControl(VolumeState.ON)
            } else if (volumeState == VolumeState.ON) {
                setVolumeControl(VolumeState.OFF)
            }
        }
    }

    private fun setVolumeControl(state: VolumeState) {
        volumeState = state
        if (state == VolumeState.OFF) {
            videoPlayer!!.volume = 0f
            //animateVolumeControl()
        } else if (state == VolumeState.ON) {
            videoPlayer!!.volume = 1f
            //animateVolumeControl()
        }
    }

    /*private fun animateVolumeControl() {
        volumeControl.bringToFront()
        if (volumeState == VolumeState.OFF) {
            requestManager!!.load(R.drawable.ic_add_circle_yellow_notif)
                .into(volumeControl)
        } else if (volumeState == VolumeState.ON) {
            requestManager!!.load(R.drawable.ic_add_circle_black_24dp)
                .into(volumeControl)
        }
        volumeControl.animate().cancel()
        volumeControl.alpha = 1f
        volumeControl.animate()
            .alpha(0f)
            .setDuration(600).startDelay = 1000
    }*/

    fun setMediaObjects(mediaObjects: List<TimenoteInfoDTO>) {
        (adapter as? TimenotePagingAdapter)?.snapshot()?.items
        this.mediaObjects = mediaObjects
    }

    companion object {
        private const val TAG = "VideoPlayerRecyclerView"
    }
}