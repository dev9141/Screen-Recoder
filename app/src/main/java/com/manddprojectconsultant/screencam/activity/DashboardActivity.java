package com.manddprojectconsultant.screencam.activity;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Instrumentation;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.manddprojectconsultant.screencam.R;
import com.manddprojectconsultant.screencam.adapter.VideoListAdapter;
import com.manddprojectconsultant.screencam.databinding.ActivityDashboardBinding;
import com.manddprojectconsultant.screencam.fragment.HorizontalListFragment;
import com.manddprojectconsultant.screencam.fragment.VerticalVideoListFragment;
import com.manddprojectconsultant.screencam.model.VideoModel;
import com.manddprojectconsultant.screencam.service.FloatingViewService;
import com.manddprojectconsultant.screencam.utils.SPVariables;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;
public class DashboardActivity extends AppCompatActivity {
    
    ActivityDashboardBinding binding;
    public static final String PREF_KEY_FIRST_START = "PREF_KEY_FIRST_START";
    public static final int REQUEST_CODE_SETTING = 1;
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    public static final int RequestPermissionCode = 7;
    public int width, height, heightU, heightB = 0;
    String SHOWCASE_ID="custom example";
    VideoListAdapter adaper;

    ArrayList<VideoModel> lstVideo;
    VideoModel videoModel = new VideoModel();
    boolean firstStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Adshow();

        onClickForGridView();
        firstStart = android.preference.PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(PREF_KEY_FIRST_START, true);
        FloatingViewService.dashboardActivity = this;
        boolean aa = getIntent().hasExtra("OpenHome");
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        Log.e("Widget Height:", " " + height);
        //1440 x 2621       1440 x 2541     720 x 1184
        heightU = ((20 * height) / 100);
        heightB = ((90 * height) / 100);

        lstVideo = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        }
        //else {
        if (CheckingPermissionIsEnabledOrNot()) {
            //Entered in if Permission is granted
            /**
             *
             * 0 = After Install First time open or Clear All Data from Setting, so open bubble and home activity both
             * 1 = After Install it's not First time open, so open direct bubble only
             *
             * **/
            //setAllSP if SP is empty
            setSP(DashboardActivity.this);
            checkOrCreateFolder();

            initializeView();
            //loadFragment();
            new LoadVideos(DashboardActivity.this, false).execute();
        }
        else {
            RequestMultiplePermission();
        }
    }

    private void Adshow() {
        MobileAds.initialize(this,"ca-app-pub-8674673470489334~1123104705");
        AdRequest adRequest=new AdRequest.Builder().build();
        binding.adsinlistview.loadAd(adRequest);

    }

    private void onClickForGridView() {
        binding.ivlistforgridview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //loadFragment(new FirstFragment());
                Intent gridlayout = new Intent(DashboardActivity.this, GridLayoutActivity.class);
                gridlayout.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                gridlayout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(gridlayout);
                overridePendingTransition(0, 0);
                finish();
            }
        });
    }

    private void presentShowcaseSequence() {

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID);

        sequence.setOnItemShownListener(new MaterialShowcaseSequence.OnSequenceItemShownListener() {
            @Override
            public void onShow(MaterialShowcaseView itemView, int position) {
                //Toast.makeText(itemView.getContext(), "Item #" + position, Toast.LENGTH_SHORT).show();
            }
        });

        sequence.setConfig(config);
        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(binding.ivlistforgridview)
                        .setDismissText("GOT IT")
                        .setContentText("Click here for list /grid view")
                        .setMaskColour(getResources().getColor(R.color.coloryellow))

                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(binding.ivsetting)
                        .setDismissText("GOT IT")
                        .setContentText("Set Video Configuration of resolution, frame, video quality, audio, camera view")
                        .setMaskColour(getResources().getColor(R.color.coloryellow))
                        .build()
        );

        sequence.start();
    }


    private void loadFragment() {
        getSupportFragmentManager().beginTransaction().replace(binding.dashboardFrameLayout.getId(), new HorizontalListFragment()).commit();
    }

    protected void checkOrCreateFolder() {
        try {
            String folderName = getResources().getString(R.string.main_folder_name);
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)+File.separator+folderName);
            if (!file.exists()) {
                file.mkdirs();
            }
            File recordFolder = new File(file, "Recording");
            if (!recordFolder.exists()) {
                recordFolder.mkdirs();
            }
            File tempFolder = new File(file, ".temp");
            if (!tempFolder.exists()) {
                tempFolder.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<VideoModel> getVideoList() throws IOException {
        ArrayList<VideoModel> lstVideoModel = new ArrayList<>();
        //File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String folderName = getResources().getString(R.string.main_folder_name);
        File mainFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)+File.separator+folderName);

        File file = new File(mainFile, "Recording");
        File temp_file = new File(mainFile, ".temp");
        if (!temp_file.exists()) temp_file.mkdirs();

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
                    videoModel.vDuration = gethms(checkVideoDurationValidation(DashboardActivity.this, Uri.parse(f.getPath())));
                    videoModel.vName = f.getName();
                    /*if (!tf.exists()) {
                        if (!tf.createNewFile()) {
                            Log.d(DashboardActivity.class.getName(), "Cannot not create the file");
                        }
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
                    videoModel.vTempPath = tf.getPath();*/
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

    @Override
    protected void onResume() {
        super.onResume();
        //loadRecordedVideos();
        if (CheckingPermissionIsEnabledOrNot()) {
            new LoadVideos(DashboardActivity.this, false).execute();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //loadRecordedVideos();
        new LoadVideos(DashboardActivity.this, false).execute();
    }

    private void initializeView() {
        presentShowcaseSequence();
        DashboardActivity.this.startService(new Intent(DashboardActivity.this, FloatingViewService.class));
    }

    public void SettingClick(View view) {

        if (view==binding.ivsetting) {
            presentShowCaseForSettings(0);
        }
        startActivity(new Intent(this, SettingActivity.class));
    }

    private void presentShowCaseForSettings(int i) {

        new MaterialShowcaseView.Builder(this)
                .setTarget(binding.ivsetting)
                .setGravity(32)
                .setContentText("Settings, When you click on this button it show Settings.")
                .setDismissText("GOT IT")
                .setShapePadding(30)
                .setDelay(3000)
                .setTooltipMargin(30)
                .setSequence(true)
                .setDismissOnTouch(true)
                .setContentTextColor(getResources().getColor(R.color.colorforoffwhite))
                .setMaskColour(getResources().getColor(R.color.coloryellow))
                .setDelay(i) // optional but starting animations immediately in onCreate can make them choppy
                .singleUse(SHOWCASE_ID) // provide a unique ID used to ensure it is only shown once
                .show();


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
                progressDialog = new ProgressDialog(context);
                progressDialog.setCancelable(false);
                progressDialog.setTitle("Loading");
               // progressDialog.setIcon(getResources().getDrawable(R.drawable.loading));
               //progressDialog.setProgressDrawable(getResources().getDrawable(R.drawable.loading));

               progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.loading));

               // progressDialog.setProgress(getResources().getDrawable(R.drawable.loading));
                progressDialog.setMessage("Please wait for few seconds while loading the files.... ");
                Activity act = DashboardActivity.this;

                if(!act.isFinishing() && act.isDestroyed()){
                    progressDialog.show();
                }
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
                    adaper = new VideoListAdapter(lstVideo, context);
                    binding.rvVideoList1.setAdapter(adaper);
                    binding.rvVideoList1.setLayoutManager(new LinearLayoutManager(context));
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
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void setSP(Context context) {
        try {
            if (SPVariables.getString("Resolution", context) == null || SPVariables.getString("Resolution", context).isEmpty()) {
                SPVariables.setString("Resolution", "720P", context);
            }
            if (SPVariables.getString("Quality", context) == null || SPVariables.getString("Quality", context).isEmpty()) {
                SPVariables.setString("Quality", "HD", context);
            }
            if (SPVariables.getString("FPS", context) == null || SPVariables.getString("FPS", context).isEmpty()) {
                SPVariables.setString("FPS", "60FPS", context);
            }
            if (SPVariables.getString("Orientation", context) == null || SPVariables.getString("Orientation", context).isEmpty()) {
                SPVariables.setString("Orientation", "Portrait", context);
            }
            if (SPVariables.getString("CameraFacing", context) == null || SPVariables.getString("CameraFacing", context).isEmpty()) {
                SPVariables.setString("CameraFacing", "Front", context);
            }
            if (SPVariables.getString("CameraFrame", context) == null || SPVariables.getString("CameraFrame", context).isEmpty()) {
                SPVariables.setString("CameraFrame", "Round", context);
            }
            if (SPVariables.getString("CameraPreview", context) == null || SPVariables.getString("CameraPreview", context).isEmpty()) {
                SPVariables.setString("CameraPreview", "Medium", context);
            }
            if (SPVariables.getString("RecordAudio", context) == null || SPVariables.getString("RecordAudio", context).isEmpty()) {
                SPVariables.setString("RecordAudio", "TRUE", context);
            }
            if (SPVariables.getString("CountDown", context) == null || SPVariables.getString("CountDown", context).isEmpty()) {
                SPVariables.setString("CountDown", "TRUE", context);
            }
            if (SPVariables.getString("ShowBubble", context) == null || SPVariables.getString("ShowBubble", context).isEmpty()) {
                SPVariables.setString("ShowBubble", "TRUE", context);
            }
            if (SPVariables.getString("RecordStartOrStop", context) == null || SPVariables.getString("RecordStartOrStop", context).isEmpty()) {
                SPVariables.setString("RecordStartOrStop", "NOTSTARTED", context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        ActivityCompat.requestPermissions(DashboardActivity.this, new String[]
                {
                        CAMERA,
                        RECORD_AUDIO,
                        WRITE_EXTERNAL_STORAGE
                }, RequestPermissionCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_SETTING){
            if(CheckingPermissionIsEnabledOrNot()) {
                Intent i = new Intent(this, DashboardActivity.class);
                startActivity(i);
                finish();
                if (!firstStart) {
                    finish();
                }
            }
            else {
                finish();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean RecordAudioPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean WriteExternalStoragePermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    if (CameraPermission && WriteExternalStoragePermission && RecordAudioPermission) {
                        //Toast.makeText(ActivityLogin.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(this, DashboardActivity.class);
                        startActivity(i);
                        finish();
                        if (!firstStart) {
                            finish();
                        }
                    } else {
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(DashboardActivity.this);
                        builder.setTitle("Screen Cam");
                        builder.setMessage("Please accept required permissions from app settings first");
                        builder.setCancelable(false);
                        builder.setIcon(R.mipmap.ic_launcher_foreground);
                        builder.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.fromParts("package", DashboardActivity.this.getPackageName(), null));
                                //startActivityIntent.launch(intent);
                                startActivityForResult(intent, REQUEST_CODE_SETTING);
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                Toast.makeText(DashboardActivity.this, "Permission Denied 1", Toast.LENGTH_SHORT).show();
                            }
                        });
                        builder.show();
                    }
                }
                break;
        }
    }

    ActivityResultLauncher<Intent> startActivityIntent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), (result) -> {
                if(result.getResultCode() == Activity.RESULT_OK){
                    Intent i = new Intent(this, DashboardActivity.class);
                    startActivity(i);
                    finish();
                    if (!firstStart) {
                        finish();
                    }
                }
    });
}
