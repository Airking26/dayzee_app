package com.dayzeeco.dayzee.video.video_player_manager.manager;

import android.content.res.AssetFileDescriptor;
import android.view.View;

import com.dayzeeco.dayzee.video.video_player_manager.meta.MetaData;
import com.dayzeeco.dayzee.video.video_player_manager.ui.VideoPlayerView;


public interface VideoPlayerManager<T extends MetaData> {

    /**
     * Call it if you have direct url or path to video source
     * @param metaData - optional Meta Data
     * @param videoPlayerView - the actual video player
     * @param videoUrl - the link to the video source
     */
    void playNewVideo(T metaData, VideoPlayerView videoPlayerView, String videoUrl);

    /**
     * Call it if you have video source in assets directory
     * @param metaData - optional Meta Data
     * @param videoPlayerView - the actual video player
     * @param assetFileDescriptor -The asset descriptor of the video file
     */
    void playNewVideo(T metaData, VideoPlayerView videoPlayerView, AssetFileDescriptor assetFileDescriptor);

    /**
     * Call it if you need to stop any playback that is currently playing
     */
    void stopAnyPlayback();

    /**
     * Call it if you no longer need the player
     */
    void resetMediaPlayer();
}
