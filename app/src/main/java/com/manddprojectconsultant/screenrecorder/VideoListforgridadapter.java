package com.manddprojectconsultant.screenrecorder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
public class VideoListforgridadapter extends RecyclerView.Adapter<VideoListforgridadapter.ViewHolder> {
    ArrayList<VideoModel> lstVideo = new ArrayList<>();
    Context context;

    public VideoListforgridadapter(ArrayList<VideoModel> lstVideo, Context context) {
        this.lstVideo = lstVideo;
        this.context = context;
    }

    @NonNull
    @Override
    public VideoListforgridadapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_lyout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull VideoListforgridadapter.ViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return lstVideo.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
