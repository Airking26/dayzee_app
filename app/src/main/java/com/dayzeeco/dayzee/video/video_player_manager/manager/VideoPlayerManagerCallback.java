package com.dayzeeco.dayzee.video.video_player_manager.manager;

import com.dayzeeco.dayzee.video.video_player_manager.PlayerMessageState;
import com.dayzeeco.dayzee.video.video_player_manager.meta.MetaData;
import com.dayzeeco.dayzee.video.video_player_manager.ui.VideoPlayerView;

public interface VideoPlayerManagerCallback {

    void setCurrentItem(MetaData currentItemMetaData, VideoPlayerView newPlayerView);

    void setVideoPlayerState(VideoPlayerView videoPlayerView, PlayerMessageState playerMessageState);

    PlayerMessageState getCurrentPlayerState();
}
