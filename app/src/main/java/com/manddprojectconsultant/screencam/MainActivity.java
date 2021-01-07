package com.manddprojectconsultant.screencam;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;
import uk.co.deanwild.materialshowcaseview.shape.CircleShape;
import uk.co.deanwild.materialshowcaseview.shape.RectangleShape;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
public class MainActivity extends AppCompatActivity {
    public static final String PREF_KEY_FIRST_START = "com.heinrichreimersoftware.materialintro.demo.PREF_KEY_FIRST_START";
    public static final int REQUEST_CODE_INTRO = 1;
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    public static final int RequestPermissionCode = 7;
    public int width, height, heightU, heightB = 0;
    RecyclerView rvVideoList;
    String SHOWCASE_ID="custom example";
    ImageView ivsettings, ivlistforgridview;
    VideoListAdaper adaper;
    AdView adsinlistview;

    ArrayList<VideoModel> lstVideo;
    VideoModel videoModel = new VideoModel();
    boolean firstStart;
    PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





        ivlistforgridview = findViewById(R.id.ivlistforgridview);



        //Ads

        adsinlistview=findViewById(R.id.adsinlistview);

        Adshow();






        onclickforgridview();
        firstStart = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(PREF_KEY_FIRST_START, true);
        FloatingViewService.mainActivity = this;
        boolean aa = getIntent().hasExtra("OpenHome");
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        Log.e("Widget Height:", " " + height);
        //1440 x 2621       1440 x 2541     720 x 1184
        //rvVideoList.setVisibility(View.VISIBLE);
        heightU = ((20 * height) / 100);
        heightB = ((90 * height) / 100);
        Log.e("Widget heightU:", " " + heightU);
        Log.e("Widget heightB:", " " + heightB);
//        int heightU= ((20*height)/100);
//        int heightB= ((80*height)/100);
        //508 - 2032
//        findViewById(R.id.rootLayout).setBackgroundColor(getResources().getColor(R.color.transpermt));
        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        display.getRealMetrics(realDisplayMetrics);
        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;
        Log.e("Widget realHeight:", " " + realHeight);
        Log.e("Widget displayHeight:", " " + displayHeight);
        rvVideoList = findViewById(R.id.rvVideoList1);
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
            //Toast.makeText(ActivityLogin.this, "All Permissions Granted Successfully", Toast.LENGTH_SHORT).show();
            /**
             *
             * 0 = After Install First time open or Clear All Data from Setting, so open bubble and home activity both
             * 1 = After Install it's not First time open, so open direct bubble only
             *
             * **/
            //setAllSP if SP is empty
            setSP(MainActivity.this);
            checkOrCreateFolder();
            /*if (firstStart) {
                //Toast.makeText(MainActivity.this, "intro", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, IntroActivity.class);
                startActivityForResult(intent, REQUEST_CODE_INTRO);
                if (introActivity.isIntroDone) {
                    initializeView();
                }
            } else {*/
            //Toast.makeText(MainActivity.this, "1", Toast.LENGTH_SHORT).show();
            //loadRecordedVideos();
            //Toast.makeText(MainActivity.this, "2", Toast.LENGTH_SHORT).show();
            //new LoadVideos(MainActivity.this, true).execute();
            initializeView();
            new LoadVideos(MainActivity.this, false).execute();
            //new openBubble(MainActivity.this).execute();
            //}
//                Intent i = new Intent(this, MainActivity.class);
//                startActi0vity(i);
//                finish();
        }
        // If, If permission is not enabled then else condition will execute.
        else {
            //Calling method to enable permission.
            RequestMultiplePermission();
        }
        //}
    }

    private void Adshow() {
        MobileAds.initialize(this,"ca-app-pub-8674673470489334~1123104705");
        AdRequest adRequest=new AdRequest.Builder().build();
        adsinlistview.loadAd(adRequest);

    }

    private void onclickforgridview() {
        ivlistforgridview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //loadFragment(new FirstFragment());



                Intent gridlayout = new Intent(MainActivity.this, GridLayoutActivity.class);
                gridlayout.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(gridlayout);
                overridePendingTransition(0, 0);





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
                        .setTarget(ivlistforgridview)
                        .setDismissText("GOT IT")
                        .setContentText("Click here for list /grid view")
                        .setMaskColour(getResources().getColor(R.color.coloryellow))

                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(ivsettings)
                        .setDismissText("GOT IT")
                        .setContentText("Set Video Configuration of resolution, frame, video quality, audio, camera view")
                        .setMaskColour(getResources().getColor(R.color.coloryellow))
                        .build()
        );

        sequence.start();

    }


    private void loadFragment(Fragment gridLayoutFragment) {
      /*  FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, gridLayoutFragment);
        fragmentTransaction.commit(); // save the changes*/
    }

    /*@Override
    protected void onUserLeaveHint() {
        Toast.makeText(introActivity, "Home Button Pressed", Toast.LENGTH_SHORT).show();
        super.onUserLeaveHint();
    }*/

    protected void checkOrCreateFolder() {
        try {
            String FolderName = getResources().getString(R.string.main_folder_name);
            File file = new File(Environment.getExternalStorageDirectory(), FolderName);
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
                    videoModel.vDuration = gethms(checkVideoDurationValidation(MainActivity.this, Uri.parse(f.getPath())));
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

    public static long checkVideoDurationValidation(Context context, Uri uri) {
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
            new LoadVideos(MainActivity.this, false).execute();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //loadRecordedVideos();
        new LoadVideos(MainActivity.this, false).execute();
    }


/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_finish, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        */
/*if (item.getItemId() == R.id.menu_item_reset_first_start) {
            PreferenceManager.getDefaultSharedPreferences(this).edit()
                    .putBoolean(PREF_KEY_FIRST_START, true)
                    .apply();
            return true;
        }*//*


        if (item.getItemId() == R.id.menu_item_setting) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
*/

    private void initializeView() {
        ivsettings = findViewById(R.id.ivsetting);
        presentShowcaseSequence();





        MainActivity.this.startService(new Intent(MainActivity.this, FloatingViewService.class));
        //finish();
        /*if (SPVariables.getInt("AFTER_INSTALL_FIRST_OPEN", MainActivity.this) == 0) {
            SPVariables.setInt("AFTER_INSTALL_FIRST_OPEN", 1, MainActivity.this);
        } else {
            if (!getIntent().hasExtra("OpenHome")) {
                finish();
            }
        }*/
        /*findViewById(R.id.notify_me).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService(new Intent(MainActivity.this, FloatingViewService.class));
                finish();
            }
        });*/
    }

    public void SettingClick(View view) {

        if (view.getId()==R.id.ivsetting) {
            presentShowCaseforSettings(0);
        }
        startActivity(new Intent(this, SettingActivity.class));
    }

    private void presentShowCaseforSettings(int i) {

        new MaterialShowcaseView.Builder(this)
                .setTarget(ivsettings)
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

    class openBubble extends AsyncTask {
        Context context;

        public openBubble(Context context) {
            this.context = context;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }
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
                    adaper = new VideoListAdaper(lstVideo, context);
                    rvVideoList.setAdapter(adaper);
                    rvVideoList.setLayoutManager(new LinearLayoutManager(context));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            boolean isServiceRunning = isMyServiceRunning(FloatingViewService.class);
            lstVideo = getVideoList();
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
        ActivityCompat.requestPermissions(MainActivity.this, new String[]
                {
                        CAMERA,
                        RECORD_AUDIO,
                        WRITE_EXTERNAL_STORAGE
                }, RequestPermissionCode);
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
                        Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
}
