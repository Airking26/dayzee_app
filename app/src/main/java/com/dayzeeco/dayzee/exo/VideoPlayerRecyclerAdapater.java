package com.dayzeeco.dayzee.exo;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.dayzeeco.dayzee.R;
import com.dayzeeco.dayzee.video.VideoAdapter;

import java.util.ArrayList;

public class VideoPlayerRecyclerAdapater extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<MediaObject> mediaObjects;
    private RequestManager requestManager;


    public VideoPlayerRecyclerAdapater(ArrayList<MediaObject> mediaObjects, RequestManager requestManager) {
        this.mediaObjects = mediaObjects;
        this.requestManager = requestManager;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if(i == 0)
        return new VideoPlayerViewHolder(
                LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_video_list_item, viewGroup, false));
        else return new VideoAdapter.ImageViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_test, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if(viewHolder.getItemViewType() == 0)
        ((VideoPlayerViewHolder)viewHolder).onBind(mediaObjects.get(i), requestManager);
        else ((VideoAdapter.ImageViewHolder)viewHolder).bindImage(mediaObjects.get(i).getThumbnail());
    }

    @Override
    public int getItemViewType(int position) {
        if(mediaObjects.get(position).getType() == 0) return 0;
        else return 1;
    }

    @Override
    public int getItemCount() {
        return mediaObjects.size();
    }

}