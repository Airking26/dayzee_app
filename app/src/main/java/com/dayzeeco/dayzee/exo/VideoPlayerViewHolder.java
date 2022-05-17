package com.dayzeeco.dayzee.exo;

import static com.google.android.exoplayer2.C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.dayzeeco.dayzee.CustomApplicationClass;
import com.dayzeeco.dayzee.R;
import com.dayzeeco.dayzee.model.TimenoteInfoDTO;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheKeyFactory;
import com.google.android.exoplayer2.upstream.cache.CacheUtil;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;

public class VideoPlayerViewHolder extends RecyclerView.ViewHolder {

    SimpleExoPlayerView exoPlayerView;
    SimpleExoPlayer exoPlayer;
    View parent;
    RequestManager requestManager;
    private long playbackPosition;
    private int currentWindow;
    private boolean playWhenReady;

    public VideoPlayerViewHolder(View itemView) {
        super(itemView);
        parent = itemView;
        exoPlayerView = itemView.findViewById(R.id.exo);
    }

    public void onBind(TimenoteInfoDTO event, RequestManager requestManager) {
        if (isPlaying()) releasePlayer();
        initPlayer(event.getVideo(), itemView.getContext());
    }

    private void initPlayer(String url, Context context) {
        try {
            exoPlayer =  new SimpleExoPlayer.Builder(context).build();
            //exoPlayer.setVideoScalingMode(VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            exoPlayerView.setPlayer(exoPlayer);
            exoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
            String urlCached = CustomApplicationClass.getProxy(context).getProxyUrl(url);
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                    context, Util.getUserAgent(context, "VideoPlayer"));
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(urlCached));
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(true);
        } catch (Exception e) {
            Log.e("MainAcvtivity", " exoplayer error " + e.toString());
        }


    }

    private boolean isPlaying() {
        return exoPlayer != null
                && exoPlayer.getPlaybackState() != Player.STATE_ENDED
                && exoPlayer.getPlaybackState() != Player.STATE_IDLE
                && exoPlayer.getPlayWhenReady();
    }

    private void releasePlayer() {
        if (exoPlayer != null) {
            playbackPosition = exoPlayer.getCurrentPosition();
            currentWindow = exoPlayer.getCurrentWindowIndex();
            playWhenReady = exoPlayer.getPlayWhenReady();
            exoPlayer.release();
            exoPlayer = null;
        }
    }


}