package com.dayzeeco.dayzee.video.video_player_manager.manager;


import com.dayzeeco.dayzee.video.video_player_manager.meta.MetaData;
import com.dayzeeco.dayzee.video.video_player_manager.ui.VideoPlayerView;

public interface VideoItem {
    void playNewVideo(MetaData currentItemMetaData, VideoPlayerView player, VideoPlayerManager<MetaData> videoPlayerManager);
    void stopPlayback(VideoPlayerManager videoPlayerManager);
}
