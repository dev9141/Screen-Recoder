package com.manddprojectconsultant.screenrecorder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
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
    public void onBindViewHolder(@NonNull final VideoListforgridadapter.ViewHolder holder, final int position) {
        final String VideoPath = lstVideo.get(position).vPath;
        //holder.ivVideoThum.setImageBitmap(bm);
        holder.ivVideoGridThum.setImageURI(Uri.parse(lstVideo.get(position).vTempPath));
        holder.ivVideoGridThum.setScaleType(ImageView.ScaleType.FIT_CENTER);
        //holder.ivVideoThum.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        //holder.ivVideoThum.setBackgroundColor(Color.rgb(200, 200, 200));
        holder.ivVideoGridThum.setBackgroundColor(Color.parseColor("#FFBB4F"));
        holder.tvVideoGridDuration.setText(lstVideo.get(position).vDuration);
        holder.tvVideogridlistname.setText(lstVideo.get(position).vName);
        holder.tvVideogridFileSize.setText(lstVideo.get(position).vSize);
        holder.tvVideogridResolution.setText(lstVideo.get(position).vResolution);
        holder.ivVideoGridPlay.setOnClickListener(new View.OnClickListener() {
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
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
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
                android.app.AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("Confirm Delete");
                alert.show();
            }
        });
        holder.ib_Share.setTag(position);
        holder.ib_Share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "Position: "+ ((int)v.getTag()+1), Toast.LENGTH_SHORT).show();
                File file = new File(lstVideo.get((int) v.getTag()).vPath);

                Uri uri = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                        ? FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file)
                        : Uri.fromFile(file);
                Intent shareIntent =
                        ShareCompat.IntentBuilder.from((Activity) context)
                                .setChooserTitle("Share to")
                                .setType("video/mp4")
                                .setStream(uri)
                                .getIntent();
                if (shareIntent.resolveActivity(context.getPackageManager()) != null) {
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    // shareIntent.setPackage("com.whatsapp");
                    context.startActivity(shareIntent);
                }
            }
        });
        holder.ib_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View vv = view;
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context,R.style.MyRounded_MaterialComponents_MaterialAlertDialog);
                ViewGroup viewGroup = view.findViewById(android.R.id.content);

                View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.custom_dialog_remane, viewGroup, false);
                String ss = lstVideo.get(position).vPath;
                final File file = new File(ss);
                final EditText etRename = dialogView.findViewById(R.id.etRename);

                String name = file.getName().toString();
                name = name.substring(0, name.length() - 4);
                etRename.setText(name);
                builder.setMessage("Do you want to rename this recording file ?")
                        .setCancelable(false)
                        .setPositiveButton("Rename", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                File dir = file.getParentFile();
                                String newFileName = etRename.getText().toString() + ".mp4";
                                //File newFile;
                                //if(file.exists()){
                                File newFile = new File(dir + "/" + newFileName);
                                if (!file.getName().equals(newFileName)) {
                                    //rename(file, newFile);
                                    boolean a = file.getParentFile().exists() && file.exists() && file.renameTo(newFile);

                                    context.startActivity(new Intent(context, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                                    //notifyDataSetChanged();
                                } else {
                                    Toast.makeText(context, "File Name is Same", Toast.LENGTH_LONG).show();
                                }
                                //notifyItemChanged(position);
                                notifyDataSetChanged();
                                //}
                                //int aa = (int) vv.getTag();
                                //lstVideo.remove((int) view.getTag());
                                //notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                            }
                        });
                builder.setView(dialogView);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return lstVideo.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivVideoGridThum;
        TextView tvVideoGridDuration, tvVideogridlistname, tvVideogridFileSize, tvVideogridResolution;
        ImageView ib_Edit, ib_Share, ib_Delete;
        LinearLayout ivVideoGridPlay;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivVideoGridThum = (ImageView) itemView.findViewById(R.id.ivVideoGridThum);
            tvVideoGridDuration = (TextView) itemView.findViewById(R.id.tvVideoGridDuration);
            tvVideogridlistname = (TextView) itemView.findViewById(R.id.tvVideogridlistname);
            tvVideogridFileSize = (TextView) itemView.findViewById(R.id.tvVideogridFileSize);
            tvVideogridResolution = (TextView) itemView.findViewById(R.id.tvVideogridResolution);
            ib_Edit = (ImageView) itemView.findViewById(R.id.ib_Edit);
            ib_Share = (ImageView) itemView.findViewById(R.id.ib_Share);
            ib_Delete = (ImageView) itemView.findViewById(R.id.ib_Delete);
            ivVideoGridPlay = (LinearLayout) itemView.findViewById(R.id.ivVideoGridPlay);
        }
    }
}
