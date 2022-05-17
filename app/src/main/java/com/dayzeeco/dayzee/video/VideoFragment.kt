package com.dayzeeco.dayzee.video

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.dayzeeco.dayzee.R
import com.dayzeeco.dayzee.video.video_player_manager.Config
import com.dayzeeco.dayzee.video.video_player_manager.manager.SingleVideoPlayerManager
import com.dayzeeco.dayzee.video.video_player_manager.manager.VideoPlayerManager
import com.dayzeeco.dayzee.video.video_player_manager.meta.MetaData
import kotlinx.android.synthetic.main.fragment_video.*
import java.io.IOException
import java.util.*


class VideoFragment : Fragment() {

    private val mList: ArrayList<UrlVideoItem> = ArrayList()
    private val mVideoPlayerManager: VideoPlayerManager<MetaData> = SingleVideoPlayerManager { }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        try {
            mList.add(UrlVideoItem("http://techslides.com/demos/sample-videos/small.mp4",mVideoPlayerManager, "https://cdn.mos.cms.futurecdn.net/6Kr3EWY2GKe738gpELRSAX.jpg"))
            mList.add(UrlVideoItem(null,mVideoPlayerManager, "https://cdn.mos.cms.futurecdn.net/6Kr3EWY2GKe738gpELRSAX.jpg"))
            mList.add(UrlVideoItem("http://techslides.com/demos/sample-videos/small.mp4",mVideoPlayerManager, "https://cdn.mos.cms.futurecdn.net/6Kr3EWY2GKe738gpELRSAX.jpg"))
            mList.add(UrlVideoItem("http://techslides.com/demos/sample-videos/small.mp4",mVideoPlayerManager, "https://cdn.mos.cms.futurecdn.net/6Kr3EWY2GKe738gpELRSAX.jpg"))
            mList.add(UrlVideoItem(null,mVideoPlayerManager, "https://cdn.mos.cms.futurecdn.net/6Kr3EWY2GKe738gpELRSAX.jpg"))
            mList.add(UrlVideoItem(null,mVideoPlayerManager, "https://cdn.mos.cms.futurecdn.net/6Kr3EWY2GKe738gpELRSAX.jpg"))
            mList.add(UrlVideoItem("http://techslides.com/demos/sample-videos/small.mp4",mVideoPlayerManager, "https://cdn.mos.cms.futurecdn.net/6Kr3EWY2GKe738gpELRSAX.jpg"))
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        val rootView: View = inflater.inflate(R.layout.fragment_video, container, false)
       /* mRecyclerView = rootView.findViewById(R.id.recycler_view) as RecyclerView
        mRecyclerView.setHasFixedSize(false)
        mLayoutManager = LinearLayoutManager(activity)
        mRecyclerView.layoutManager = mLayoutManager
        val videoRecyclerViewAdapter = VideoAdapter(mVideoPlayerManager, mList, requireContext())
        mRecyclerView.adapter = videoRecyclerViewAdapter
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, scrollState: Int) {
                mScrollState = scrollState
                if (scrollState == RecyclerView.SCROLL_STATE_IDLE && mList.isNotEmpty()) {
                    mVideoVisibilityCalculator.onScrollStateIdle(
                        mItemsPositionGetter,
                        mLayoutManager.findFirstVisibleItemPosition(),
                        mLayoutManager.findLastVisibleItemPosition()
                    )
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (mList.isNotEmpty()) {
                    mVideoVisibilityCalculator.onScroll(
                        mItemsPositionGetter,
                        mLayoutManager.findFirstVisibleItemPosition(),
                        mLayoutManager.findLastVisibleItemPosition() - mLayoutManager.findFirstVisibleItemPosition() + 1,
                        mScrollState
                    )
                }
            }
        })
        mItemsPositionGetter = RecyclerViewItemPositionGetter(mLayoutManager, mRecyclerView)*/
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initRecyclerView()
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        recycler_view.layoutManager = layoutManager
    }


    private fun initGlide(): RequestManager? {
        val options: RequestOptions = RequestOptions()
            .placeholder(R.drawable.adaptive_icon)
            .error(R.drawable.adaptive_icon)
        return Glide.with(this)
            .setDefaultRequestOptions(options)
    }

    override fun onResume() {
        super.onResume()
        /*if (mList.isNotEmpty()) {
            // need to call this method from list view handler in order to have filled list
            mRecyclerView.post {
                mVideoVisibilityCalculator.onScrollStateIdle(
                    mItemsPositionGetter,
                    mLayoutManager.findFirstVisibleItemPosition(),
                    mLayoutManager.findLastVisibleItemPosition()
                )
            }
        }*/
    }

    override fun onStop() {
        super.onStop()
        // we have to stop any playback in onStop
            //mVideoPlayerManager.resetMediaPlayer()
    }

    companion object {
        private val SHOW_LOGS: Boolean = Config.SHOW_LOGS
        private val TAG = VideoFragment::class.java.simpleName
    }
}