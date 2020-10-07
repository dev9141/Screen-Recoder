package com.example.screenrecorder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class VideoListAdaper extends RecyclerView.Adapter<VideoListAdaper.ViewHolder>{
    ArrayList<VideoModel> lstVideo = new ArrayList<>();
    Context context;

    public VideoListAdaper(ArrayList<VideoModel> lstVideo, Context context) {
        this.lstVideo = lstVideo;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_video_listview, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        /*File file = new File(lstVideo.get(position).vPath);

        if(file.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            holder.ivVideoThum.setImageBitmap(myBitmap);

        }
        else {
            holder.ivVideoThum.setImageDrawable(context.getResources().getDrawable(R.drawable.video_thum));
        }*/

        //holder.ivVideoThum.setImageURI(Uri.parse(lstVideo.get(position).vPath));

       // Bitmap bm = getThumbnail(lstVideo.get(position).vPath);
        final String VideoPath = lstVideo.get(position).vPath;
        //holder.ivVideoThum.setImageBitmap(bm);
        holder.ivVideoThum.setImageURI(Uri.parse(lstVideo.get(position).vTempPath));

        holder.ivVideoThum.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        //holder.ivVideoThum.setBackgroundColor(Color.rgb(200, 200, 200));
        holder.ivVideoThum.setBackgroundColor(Color.parseColor("#f0ecec"));

        holder.tvVideoDuration.setText(lstVideo.get(position).vDuration);
        holder.tvVideoName.setText(lstVideo.get(position).vName);
        holder.tvVideoFileSize.setText(lstVideo.get(position).vSize);
        holder.tvVideoResolution.setText(lstVideo.get(position).vResolution);

        holder.ivVideoPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VideoPlayerActivity.class);
                intent.putExtra("VideoPath", VideoPath);
                intent.putExtra("VidelFileName", lstVideo.get(position).vName);
                context.startActivity(intent);
            }
        });


        holder.ib_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT).show();
            }
        });
        final int pos = position;

        holder.ib_Delete.setTag(position);
        holder.ib_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final View vv = v;


                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                //Uncomment the below code to Set the message and title from the strings.xml file
                //builder.setMessage("DO you want Delete this Recording?") .setTitle("Delete");

                //Setting message manually and performing action on button click
                builder.setMessage("Are you sure? You want to delete this recording?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                File file = new File(lstVideo.get((int) vv.getTag()).vPath);
                                file.delete();

                                //int aa = (int) vv.getTag();
                                lstVideo.remove((int) vv.getTag());
                                //int aaaaa=lstVideo.size();
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                            }
                        });
                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("Confirm Delete");
                alert.show();


                /*File file = new File(lstVideo.get((int) v.getTag()).vPath);
                file.delete();

                int aa = (int) v.getTag();
                lstVideo.remove((int) v.getTag());
                int aaaaa=lstVideo.size();
                notifyDataSetChanged();*/
            }
        });


        holder.ib_Share.setTag(position);
        holder.ib_Share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "Position: "+ ((int)v.getTag()+1), Toast.LENGTH_SHORT).show();
                File file = new File(lstVideo.get((int) v.getTag()).vPath);

                /*if(file.exists()){
                    Toast.makeText(context, "file: "+ file.getName()+"\n"+file.getPath(), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(context, "file: not exist", Toast.LENGTH_SHORT).show();
                }*/

                Intent intent = new Intent(Intent.ACTION_SEND);

                //Uri uri = Uri.fromFile(file);
                //Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);

                Uri uri = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                        ? FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file)
                        :Uri.fromFile(file);

                intent.putExtra(Intent.EXTRA_STREAM, uri); // for media share
                intent.setType("video/mp4");
                context.startActivity(intent);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return lstVideo.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ivVideoThum;
        TextView tvVideoDuration;
        TextView tvVideoName;
        TextView tvVideoFileSize;
        TextView tvVideoResolution;

        ImageButton ib_Edit;
        ImageButton ib_Share;
        ImageButton ib_Delete;

        LinearLayout ivVideoPlay;


        public ViewHolder(View itemView) {
            super(itemView);

            ivVideoThum = (ImageView) itemView.findViewById(R.id.ivVideoThum);
            tvVideoDuration = (TextView) itemView.findViewById(R.id.tvVideoDuration);
            tvVideoName = (TextView) itemView.findViewById(R.id.tvVideoName);
            tvVideoFileSize = (TextView) itemView.findViewById(R.id.tvVideoFileSize);
            tvVideoResolution = (TextView) itemView.findViewById(R.id.tvVideoResolution);

            ib_Edit = (ImageButton) itemView.findViewById(R.id.ib_Edit);
            ib_Share = (ImageButton) itemView.findViewById(R.id.ib_Share);
            ib_Delete = (ImageButton) itemView.findViewById(R.id.ib_Delete);

            ivVideoPlay = (LinearLayout) itemView.findViewById(R.id.ivVideoPlay);
        }
    }

    public Bitmap getThumbnail(String filePath){
        Bitmap bmThumbnail;

//MICRO_KIND, size: 96 x 96 thumbnail
        //bmThumbnail = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MICRO_KIND);


// MINI_KIND, size: 512 x 384 thumbnail
        //bmThumbnail = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MINI_KIND);
        bmThumbnail = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
        //imageview_mini.setImageBitmap(bmThumbnail);

        return bmThumbnail;
    }
}
