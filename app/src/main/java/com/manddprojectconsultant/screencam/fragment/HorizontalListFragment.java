package com.manddprojectconsultant.screencam.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.manddprojectconsultant.screencam.R;
import com.manddprojectconsultant.screencam.adapter.VideoListAdapter;
import com.manddprojectconsultant.screencam.databinding.FragmentHorizontalListBinding;
import com.manddprojectconsultant.screencam.model.VideoModel;
import com.manddprojectconsultant.screencam.service.FloatingViewService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
public class HorizontalListFragment extends Fragment {
    FragmentHorizontalListBinding binding;
    VideoListAdapter adapter;
    ArrayList<VideoModel> lstVideo;
    Activity activity;

    public void setActivity(Activity activity){
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentHorizontalListBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new LoadVideos(activity, false).execute();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    class LoadVideos extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog = null;
        Context context;
        Boolean onLoad;

        public LoadVideos(Context context, Boolean onLoad) {
            this.context = context;
            this.onLoad = onLoad;
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (onLoad == false) {
                /*progressDialog = new ProgressDialog(context);
                progressDialog.setCancelable(false);
                progressDialog.setTitle("Loading");*/
                // progressDialog.setIcon(getResources().getDrawable(R.drawable.loading));
                //progressDialog.setProgressDrawable(getResources().getDrawable(R.drawable.loading));

               // progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.loading));

                // progressDialog.setProgress(getResources().getDrawable(R.drawable.loading));
                //progressDialog.setMessage("Please wait for few seconds while loading the files.... ");
                Activity act = activity;

                //if(!act.isFinishing() && act.isDestroyed()){
                    //progressDialog.show();
                //}
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            progressDialog = null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (onLoad == false && progressDialog != null) {
                progressDialog.dismiss();
            }
            try {
                if (s.equals("Video")) {
                    adapter = new VideoListAdapter(lstVideo, context);
                    binding.rvVideoList.setAdapter(adapter);
                    binding.rvVideoList.setLayoutManager(new LinearLayoutManager(context));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            boolean isServiceRunning = isMyServiceRunning(FloatingViewService.class);
            try {
                lstVideo = getVideoList();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (lstVideo.size() > 0) {
                return "Video";
            } else {
                return "NoVideo";
            }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<VideoModel> getVideoList() throws IOException {
        ArrayList<VideoModel> lstVideoModel = new ArrayList<>();
        //File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String folderName = getResources().getString(R.string.main_folder_name);
        File mainFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)+File.separator+folderName);

        File file = new File(mainFile, "Recording");
        File temp_file = new File(mainFile, ".temp");
        File[] files = file.listFiles();
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
                    videoModel.vDuration = gethms(checkVideoDurationValidation(activity.getBaseContext(), Uri.parse(f.getPath())));
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

                    //mobile use thase app install kari joie lau
                    videoModel.vTempPath = tf.getPath();
                    videoModel.vResolution = getResolution(f.getPath());

                /*int file_size = Integer.parseInt(String.valueOf(((f.getPath().length()/1024)/1024)/1024));
                videoModel.vSize = file_size+" KB";*/
                    lstVideoModel.add(videoModel);
                }
                //if (f.getName().startsWith("EDMT") && f.getName().endsWith(".mp4") && f.length()==0){
                if (f.getName().startsWith("SC_") && f.getName().endsWith(".mp4") && f.length() == 0) {
                    f.delete();
                }
            }

            Collections.sort(lstVideoModel, new Comparator<VideoModel>() {
                @Override
                public int compare(VideoModel lhs, VideoModel rhs) {
                    return rhs.getvName().compareTo(lhs.getvName());
                }
            });
            ArrayList<String> lstVideoName = new ArrayList<>();
            for (VideoModel vm :
                    lstVideoModel) {
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
        return lstVideoModel;
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

    public static long checkVideoDurationValidation(Context context, Uri uri) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//use one of overloaded setDataSource() functions to set your data source
        retriever.setDataSource(context, uri);
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInMillisec = Long.parseLong(time);
        retriever.release();
        return timeInMillisec;
    }

}
