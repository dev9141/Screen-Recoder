package com.manddprojectconsultant.screencam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
public class GridLayoutActivity extends AppCompatActivity {
    ImageView ivlistnormallist, ivsettings;
    RecyclerView rvVideoListforgridview;
    public static final int RequestPermissionCode = 7;
    boolean firstStart;
    ArrayList<VideoModel> lstVideo;
    VideoListforgridadapter adapter;

    AdView adsingridview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_layout);








        lstVideo = new ArrayList<>();
        if (CheckingPermissionIsEnabledOrNot()) {
            init();
        }else {
            //Calling method to enable permission.
            RequestMultiplePermission();
        }


        adsingridview=findViewById(R.id.adsingridview);
        Adshow();



        ivsettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent setting = new Intent(GridLayoutActivity.this, SettingActivity.class);
                setting.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(setting);
                overridePendingTransition(0, 0);
            }
        });

        /*ivlistnormallist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //loadFragment(new FirstFragment());
                Intent gridlayout = new Intent(GridLayoutActivity.this, MainActivity.class);
                gridlayout.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(gridlayout);
                overridePendingTransition(0, 0);
            }
        });*/
    }

    private void Adshow() {
        MobileAds.initialize(this,"ca-app-pub-8674673470489334~1123104705");
        AdRequest adRequest=new AdRequest.Builder().build();
        adsingridview.loadAd(adRequest);

    }

    private void init() {
        ivlistnormallist = findViewById(R.id.ivlistnormallist);
        ivsettings = findViewById(R.id.ivsettings);
        rvVideoListforgridview = findViewById(R.id.rvVideoListforgridview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        rvVideoListforgridview.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView
        //  call the constructor of CustomAdapter to send the reference and data to Adapter
        new LoadVideos(GridLayoutActivity.this, false).execute();



        /*adapter = new VideoListforgridadapter(lstVideo, this);
        rvVideoListforgridview.setAdapter(adapter);*/
    }

    public void ListClickforDashboard(View view) {
        Intent normal = new Intent(GridLayoutActivity.this, MainActivity.class);
        normal.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(normal);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean RecordAudioPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean WriteExternalStoragePermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    if (CameraPermission && WriteExternalStoragePermission && RecordAudioPermission) {
                        //Toast.makeText(ActivityLogin.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(this, MainActivity.class);
                        startActivity(i);
                        finish();
                        if (!firstStart) {
                            finish();
                        }
                    } else {
                        finish();
                        Toast.makeText(GridLayoutActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //loadRecordedVideos();
        if (CheckingPermissionIsEnabledOrNot()) {
            new LoadVideos(GridLayoutActivity.this, false).execute();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //loadRecordedVideos();
        new LoadVideos(GridLayoutActivity.this, false).execute();
    }

    public boolean CheckingPermissionIsEnabledOrNot() {
        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        int ThirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ThirdPermissionResult == PackageManager.PERMISSION_GRANTED;
    }

    private void RequestMultiplePermission() {
        // Creating String Array with Permissions.
        ActivityCompat.requestPermissions(GridLayoutActivity.this, new String[]
                {
                        CAMERA,
                        RECORD_AUDIO,
                        WRITE_EXTERNAL_STORAGE
                }, RequestPermissionCode);
    }

    class LoadVideos extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog = null;
        Context context;
        Boolean onLoad;

        public LoadVideos(Context context, Boolean onLoad) {
            this.context = context;
            this.onLoad = onLoad;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (onLoad == false) {
                progressDialog = new ProgressDialog(context);
                progressDialog.setCancelable(false);
                progressDialog.setTitle("Loading");
                // progressDialog.setIcon(getResources().getDrawable(R.drawable.loading));
                //progressDialog.setProgressDrawable(getResources().getDrawable(R.drawable.loading));

                progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.loading));

                // progressDialog.setProgress(getResources().getDrawable(R.drawable.loading));
                progressDialog.setMessage("Please wait for few seconds while loading the files.... ");
                progressDialog.show();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (onLoad == false && progressDialog != null) {
                progressDialog.dismiss();
            }
            try {
                if (s.equals("Video")) {
                    adapter = new VideoListforgridadapter(lstVideo, context);
                    rvVideoListforgridview.setAdapter(adapter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            lstVideo = getVideoList();
            if (lstVideo.size() > 0) {
                return "Video";
            } else {
                return "NoVideo";
            }
        }
    }

    private ArrayList<VideoModel> getVideoList() {
        ArrayList<VideoModel> lstvideoModel = new ArrayList<>();
        //File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String FolderName = getResources().getString(R.string.main_folder_name);
        File file = new File(new File(Environment.getExternalStorageDirectory(), FolderName), "Recording");
        File temp_file = new File(new File(Environment.getExternalStorageDirectory(), FolderName), ".temp");
        File[] files = file.listFiles();
        File[] temp_files = temp_file.listFiles();
        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                Log.e("getVideoList", "File name: " + f.getName());
                String fileName = f.getName();
                String FName = fileName.substring(0, fileName.length() - 4) + ".jpg";
                //String fileName = FileName[0]+".jpg";
                File tf = new File(temp_file, FName);
                VideoModel videoModel = new VideoModel();
                //if (f.getName().startsWith("EDMT") && f.getName().endsWith(".mp4") && f.length()>0){
                if (f.getName().endsWith(".mp4") && f.length() > 0) {
                    //f.getName().startsWith("Rec_") &&
                    videoModel.vPath = f.getPath();
                    long size = f.length();
                    videoModel.vSize = size(size);
                    videoModel.vDuration = gethms(checkVideoDurationValidation(GridLayoutActivity.this, Uri.parse(f.getPath())));
                    videoModel.vName = f.getName();
                    if (!tf.exists()) {
                        Bitmap bmThumbnail = ThumbnailUtils.createVideoThumbnail(f.getPath(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                        try {
                            FileOutputStream out = new FileOutputStream(tf);
                            bmThumbnail.compress(Bitmap.CompressFormat.JPEG, 90, out);
                            out.flush();
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    videoModel.vTempPath = tf.getPath();
                    videoModel.vResolution = getResolution(f.getPath());

                /*int file_size = Integer.parseInt(String.valueOf(((f.getPath().length()/1024)/1024)/1024));
                videoModel.vSize = file_size+" KB";*/
                    lstvideoModel.add(videoModel);
                }
                //if (f.getName().startsWith("EDMT") && f.getName().endsWith(".mp4") && f.length()==0){
                if (f.getName().startsWith("SC_") && f.getName().endsWith(".mp4") && f.length() == 0) {
                    f.delete();
                }
            }

          /*  Collections.sort(lstvideoModel, new Comparator<VideoModel>() {
                @Override
                public int compare(VideoModel lhs, VideoModel rhs) {
                    return lhs.getvName().compareTo(rhs.getvName());
                }
            });*/
            Collections.sort(lstvideoModel, new Comparator<VideoModel>() {
                @Override
                public int compare(VideoModel lhs, VideoModel rhs) {
                    return rhs.getvName().compareTo(lhs.getvName());
                }
            });
            ArrayList<String> lstVideoName = new ArrayList<>();
            for (VideoModel vm :
                    lstvideoModel) {
                lstVideoName.add(vm.getvName());
            }

            /*for (File f :
                    files) {

                VideoModel videoModel = new VideoModel();
                if (f.getName().startsWith("EDMT") && f.getName().endsWith(".mp4") && f.length()>0){
                    videoModel.vPath = f.getPath();
                    long size = f.length();
                    videoModel.vSize = size(size);
                    videoModel.vDuration = gethms(checkVideoDurationValidation(MainActivity.this, Uri.parse(f.getPath())));
                    videoModel.vName = f.getName();

                    videoModel.vResolution = getResolution(f.getPath());

                *//*int file_size = Integer.parseInt(String.valueOf(((f.getPath().length()/1024)/1024)/1024));
                videoModel.vSize = file_size+" KB";*//*
                    lstvideoModel.add(videoModel);
                }
            }*/
        }
        return lstvideoModel;
    }

    public String size(long size) {
        DecimalFormat df = new DecimalFormat("0.00");
        float sizeKb = 1000.0f;
        float sizeMb = sizeKb * sizeKb;
        float sizeGb = sizeMb * sizeKb;
        float sizeTerra = sizeGb * sizeKb;
        if (size < sizeMb)
            return df.format(size / sizeKb) + " kB";
        else if (size < sizeGb)
            return df.format(size / sizeMb) + " MB";
        else if (size < sizeTerra)
            return df.format(size / sizeGb) + " GB";
        return "";


        /*String hrSize = "";
        double k = size/1024.0;
        double m = size/1048576.0;
        double g = size/1073741824.0;

        DecimalFormat dec = new DecimalFormat("0.00");

        if (g > 1) {
            hrSize = dec.format(g).concat("GB");
        } else if (m > 1) {
            hrSize = dec.format(m).concat("MB");
        } else {
            hrSize = dec.format(size).concat("KB");
        }
        return hrSize;*/
    }

    public static String gethms(Long millis) {
        String h = String.format("%02d", TimeUnit.MILLISECONDS.toHours(millis));
        String m = String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)));
        String s = String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        String hms = "";
        if (h.equals("00")) {
            return hms = m + ":" + s;
        } else {
            return hms = h + ":" + m + ":" + s;
        }
    }

    public static long checkVideoDurationValidation(Context context, Uri uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//use one of overloaded setDataSource() functions to set your data source
        retriever.setDataSource(context, uri);
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInMillisec = Long.parseLong(time);
        retriever.release();
        return timeInMillisec;
    }

    public static String getResolution(String path) {
        String Resolution = "";
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(path);
        String height = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        String width = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        Resolution = width + " x " + height;
        return Resolution;
    }
}