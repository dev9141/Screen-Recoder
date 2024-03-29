package com.manddprojectconsultant.screencam.activity;

import android.Manifest;
import android.app.Notification;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.CamcorderProfile;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.manddprojectconsultant.screencam.widget.CameraView;
import com.manddprojectconsultant.screencam.service.FloatingViewService;
import com.manddprojectconsultant.screencam.R;
import com.manddprojectconsultant.screencam.utils.SPVariables;
import com.squareup.seismic.ShakeDetector;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.manddprojectconsultant.screencam.service.FloatingViewService.mFloatingView;
public class BlankActivity extends AppCompatActivity implements ShakeDetector.Listener {
    private static final int REQUEST_CODE = 1000;
    private static final int REQUEST_CODE_WRITE = 1004;
    private static final int REQUEST_CODE_PAUSE = 1002;
    private static final int REQUEST_CODE_PLAY = 1003;
    private static final int REQUEST_PERMISSION = 1001;

    private static final String CHANNEL_ID = "channel_id01";
    public static final int NOTIFICATION_ID = 1;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private MediaProjectionManager mediaProjectionManager;
    private MediaProjection mediaProjection;
    private VirtualDisplay virtualDisplay;
    private MediaProjectionCallback mediaProjectionCallback;
    private static MediaRecorder mediaRecorder;
    private int mScreenDensity;
    //    private static int DISPLAY_WIDTH = CamcorderProfile.get(CamcorderProfile.QUALITY_720P).videoFrameHeight; //400;//720;
//    private static int DISPLAY_HEIGHT = CamcorderProfile.get(CamcorderProfile.QUALITY_720P).videoFrameWidth; //720;//1280;
    private static int DISPLAY_WIDTH = 400;//720;
    private static int DISPLAY_HEIGHT = 720;//1280;
    /*private static int DISPLAY_WIDTH = 720;
    private static int DISPLAY_HEIGHT = 1280;*/

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    //View
    public RelativeLayout rootLayout;
    //public static ToggleButton toggleButton;
    public static Boolean toggleButton;
    //public static VideoView videoView;
    public static String videoUri = "";
    File recordingFile;
    public static View view;
    //public CheckBox audio;
    public String audio;
    private static final String TAG = "SCREEN RECORDER";
    private long startMs = 0, endMs = 0;
    public Camera mCamera;
    public WindowManager wmCam;
    public LinearLayout camPreivew;
    String record = "";
    public static FloatingViewService floatingViewService;

    public static int c = 4;

    Handler m_handler;
    Runnable m_handlerTask ;
    int secondsLeft=0;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FloatingViewService.blankActivity = this;
        NotificationRecordingStopActivity.blankActivity = this;
        setContentView(R.layout.activity_blank);
//        Button btnPlay = findViewById(R.id.btnPlay);
//        Button btnPause = findViewById(R.id.btnPause);




        /*btnPlay.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if (mediaProjection != null) {
                    startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_CODE_PLAY);
                    return;
                }
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if (mediaProjection != null) {
                    startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_CODE_PAUSE);
                    return;
                }

            }
        });*/
//        String aaNoti = getIntent().getStringExtra("Notification");
//        if (aaNoti != null && aaNoti.equals("stop")) {
////             && SPVariables.getString("RecordStartOrStop", getApplicationContext()).equals("STARTED")
//            getIntent().removeExtra("Notification");
//            aaNoti = getIntent().getStringExtra("Notification");
//            stopRecording();
//            floatingViewService.ShowNotification("Stop");
//
//            mFloatingView.findViewById(R.id.collapse_view).setVisibility(View.VISIBLE);
//            mFloatingView.findViewById(R.id.close_btn).setVisibility(View.VISIBLE);
//            mFloatingView.findViewById(R.id.collapse_view_stop).setVisibility(View.GONE);
//
//           // startActivity(new Intent(BlankActivity.this, MainActivity.class));
//        }
        /*else {
            BlankActivity.this.finish();
        }*/
        //Shake to stop Recording
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        ShakeDetector shakeDetector = new ShakeDetector(BlankActivity.this);
        shakeDetector.start(sensorManager);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;
        mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);


        //Get Screen
        //DISPLAY_HEIGHT2 = metrics.heightPixels;
        //DISPLAY_WIDTH = metrics.widthPixels;
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        }*/
        //View
        //videoView = findViewById(R.id.videoView);
        //toggleButton = findViewById(R.id.toggleButton);
        rootLayout = findViewById(R.id.rootLayout);
        //audio = findViewById(R.id.audio);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!Settings.canDrawOverlays(BlankActivity.this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 0);
            }
        }
        if (ContextCompat.checkSelfPermission(BlankActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                + ContextCompat.checkSelfPermission(BlankActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(BlankActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(BlankActivity.this, Manifest.permission.RECORD_AUDIO)) {
                toggleButton = false;
                Snackbar.make(rootLayout, "Permissions", Snackbar.LENGTH_INDEFINITE).setAction("ENABLE", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions(BlankActivity.this,
                                new String[]{
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.RECORD_AUDIO
                                }, REQUEST_PERMISSION);
                    }
                }).show();
            } else {
                ActivityCompat.requestPermissions(BlankActivity.this,
                        new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.RECORD_AUDIO
                        }, REQUEST_PERMISSION);
            }


        } else {
            mediaRecorder = new MediaRecorder();
            //new countdown().execute();
            String recordWithCam = getIntent().getStringExtra("RecordWithCamera") == null ? "NO" : getIntent().getStringExtra("RecordWithCamera");
            if (recordWithCam.equals("YES")) {
                new FloatingCam().execute();
            }
            record = getIntent().getStringExtra("Record") == null ? "" : getIntent().getStringExtra("Record");
            if (record.equals("START")) {
                toggleButton = true;
                startRecording();
                //startRecordingWithCam();
                String aa = "";
                String bb = aa;
                //moveTaskToBack(true);
            } else {
                toggleButton = false;
                //stopRecording();
            }
            //startOrStopRecording(toggleButton);
        }
        Boolean hasPlayIntent = getIntent().hasExtra("playRecording");
        Boolean hasPauseIntent = getIntent().hasExtra("pauseRecording");
        String strPlayIntent = getIntent().getStringExtra("playRecording");
        String strPauseIntent = getIntent().getStringExtra("pauseRecording");
        if (getIntent().hasExtra("playRecording")) {
            if (mediaProjection != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_CODE_PLAY);
                }
                return;
            }
        }
        if (getIntent().hasExtra("pauseRecording")) {
            if (mediaProjection != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_CODE_PAUSE);
                }
                return;
            }
        }
    }


    public void pauseRecording() {
        if (mediaProjection != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_CODE_PAUSE);
            }
            return;
        }
    }


    public void resumeRecording() {
        if (mediaProjection != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_CODE_PLAY);
            }
            return;
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void stopRecording() throws IOException {
        try {
            mediaRecorder.stop();
            mediaRecorder.reset();
            stopRecordScreen();

            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                stopRecordScreen();
            }*/
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Toast.makeText(this, "Recording Stopped", Toast.LENGTH_SHORT).show();
        //Play in video view
        //videoView.setVisibility(View.VISIBLE);
        //videoView.setVideoURI(Uri.parse(videoUri));
        //videoView.start();

            /*videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    duration = mp.getDuration() / 1000;
                    endMs = duration;
                }
            });*/
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//use one of overloaded setDataSource() functions to set your data source
        //moveTaskToBack(false);
        //TODO uncomment original
        //retriever.setDataSource(getApplicationContext(), Uri.parse(videoUri));
        retriever.setDataSource(getApplicationContext(), Uri.parse(recordingFile.getAbsolutePath()));
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        endMs = Long.parseLong(time);
        retriever.release();
        startActivity(new Intent(BlankActivity.this, DashboardActivity.class)
                .putExtra("OpenHome", true)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                        Intent.FLAG_ACTIVITY_NO_ANIMATION));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startRecording() {
        initRecorder();
        recordScreen();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        String aa = "aa";
        String aaNoti = getIntent().getStringExtra("Notification");
    }

    @Override
    protected void onStart() {
        super.onStart();
        String aa = "aa";
    }

    @Override
    protected void onResume() {
        super.onResume();
        String aa = "aa";
        String aaNoti = getIntent().getStringExtra("Notification");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void recordScreen() {
        if (mediaProjection == null) {
            startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
            return;
        }

        virtualDisplay = createVirtualDisplay();
       mediaRecorder.start();
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private VirtualDisplay createVirtualDisplay() {
        try {
            Surface aa = null;

                aa = mediaRecorder.getSurface();

            Surface bb = aa;
        } catch (Exception e) {
            e.printStackTrace();
        }
        String aaa = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            MediaProjection mp = mediaProjection;
            MediaRecorder mr = mediaRecorder;

            return mediaProjection.createVirtualDisplay("MainActivity", DISPLAY_WIDTH, DISPLAY_HEIGHT, mScreenDensity,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
                    mediaRecorder.getSurface(), null, null);
        }
//        return mediaProjection.createVirtualDisplay("MainActivity",
//                420, 720, mScreenDensity,
//                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
//                mediaRecorder.getSurface(), null /*Callbacks*/, null
//
//                /*Handler*/);
        return null;
    }

    private void initRecorder() {
        try {
            audio = SPVariables.getString("RecordAudio", BlankActivity.this);
            boolean checkAudio = audio.equals("TRUE") ? true : false;
            if (checkAudio) {
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            }
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            int aa = MediaRecorder.VideoSource.SURFACE;
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);

            String folderName = getResources().getString(R.string.main_folder_name);
            File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)+File.separator+folderName);
            if (!directory.exists()) directory.mkdirs();
            File subFolderPath =  new File(directory
                    + File.separator + ""
                    + ".temp");
            if (!subFolderPath.exists()) subFolderPath.mkdirs();
            File file = new File(directory
                    + File.separator + ""
                    + "Recording");
            if (!file.exists()) file.mkdirs();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            String fileName = "Recording_" + sdf.format(new Date()) + ".mp4";
            try {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // Request the permission
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE);
                }
                else {
                    recordingFile = new File(file
                            + File.separator + ""
                            + fileName);
                    if (!recordingFile.createNewFile()) {
                        Log.d(TAG, "Cannot not create the file");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            CamcorderProfile profile = null;
            String resolution = SPVariables.getString("Resolution", BlankActivity.this);
            if (resolution.equals("1080P")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    profile = CamcorderProfile.get(CamcorderProfile.QUALITY_1080P);
                    DISPLAY_WIDTH = profile.videoFrameHeight;
                    DISPLAY_HEIGHT = profile.videoFrameWidth;
                } else {
                    DISPLAY_WIDTH = 1080;
                    DISPLAY_HEIGHT = 1920;
                }
            } else if (resolution.equals("720P")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    profile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
                    DISPLAY_WIDTH = profile.videoFrameHeight;
                    DISPLAY_HEIGHT = profile.videoFrameWidth;
                } else {
                    DISPLAY_WIDTH = 720;
                    DISPLAY_HEIGHT = 1280;
                }
            } else if (resolution.equals("480P")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    profile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
                    DISPLAY_WIDTH = profile.videoFrameHeight;
                    DISPLAY_HEIGHT = profile.videoFrameWidth;
                } else {
                    DISPLAY_WIDTH = 480;
                    DISPLAY_HEIGHT = 640;
                }
            } else if (resolution.equals("240P")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    profile = CamcorderProfile.get(CamcorderProfile.QUALITY_QVGA);
                    DISPLAY_WIDTH = profile.videoFrameHeight;
                    DISPLAY_HEIGHT = profile.videoFrameWidth;
                } else {
                    DISPLAY_WIDTH = 240;
                    DISPLAY_HEIGHT = 320;
                }
            } else if (resolution.equals("144P")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    profile = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);
                    DISPLAY_WIDTH = profile.videoFrameHeight;
                    DISPLAY_HEIGHT = profile.videoFrameWidth;
                } else {
                    DISPLAY_WIDTH = 144;
                    DISPLAY_HEIGHT = 176;
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //TODO uncomment after test
                //mediaRecorder.setOutputFile(new File(videoUri));
                mediaRecorder.setOutputFile(recordingFile);
            }
            else{
                //TODO uncomment after test
                //mediaRecorder.setOutputFile(videoUri);
                mediaRecorder.setOutputFile(recordingFile.getAbsolutePath());
            }
            //DISPLAY_WIDTH = profile.videoFrameHeight;
            //DISPLAY_HEIGHT= profile.videoFrameWidth;
            mediaRecorder.setVideoSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
            //mediaRecorder.setVideoSize(420, 720);
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
            int FPS = Integer.parseInt(SPVariables.getString("FPS", BlankActivity.this).substring(0, 2));
//            mediaRecorder.setVideoFrameRate(30);
            mediaRecorder.setVideoFrameRate(FPS);
            mediaRecorder.setVideoEncodingBitRate(profile != null ? profile.videoBitRate : 25000000); // 512000  //30000000
            if (checkAudio) {
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                //mediaRecorder.setAudioSamplingRate(1); //16000
            }
            String ori = SPVariables.getString("Orientation", BlankActivity.this);
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            int orientation;
            if (ori.equals("Landscape")) {
                orientation = ORIENTATIONS.get((rotation + 0));
            } else {
                orientation = ORIENTATIONS.get((rotation + 90));
            }
            mediaRecorder.setOrientationHint(orientation);
            mediaRecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //Ctrl+o



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if (requestCode != REQUEST_CODE && requestCode != REQUEST_CODE_PLAY && requestCode != REQUEST_CODE_PAUSE) {
        if (requestCode != REQUEST_CODE) {
            Toast.makeText(this, "Unk error", Toast.LENGTH_SHORT).show();
            return;
        }
       /* if (resultCode != RESULT_OK) {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            toggleButton = false;
            return;
        }*/
        if (resultCode == RESULT_CANCELED) {
            mediaProjection = null;
            String RecordwithCam = getIntent().getStringExtra("RecordWithCamera") == null ? "NO" : getIntent().getStringExtra("RecordWithCamera");
            if (RecordwithCam.equals("YES")) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
                wmCam.removeView(camPreivew);
            }
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
            builder.setSmallIcon(R.drawable.ic_v_cam);
            builder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.ic_v_cam));
            builder.setContentTitle("Screen Recorder App");
            builder.setContentText("");
            //builder.setContentText("Description of Notification");
            //builder.setPriority(NotificationCompat.PRIORITY_HIGH);
            builder.setChannelId(CHANNEL_ID);
            builder.setAutoCancel(false);
            builder.setOngoing(true);
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());

            SPVariables.setString("RecordStartOrStop", "NOTSTARTED", getApplicationContext());
            mFloatingView.findViewById(R.id.collapse_view).setVisibility(View.VISIBLE);
            mFloatingView.findViewById(R.id.collapse_view_stop).setVisibility(View.GONE);
            mFloatingView.findViewById(R.id.close_btn).setVisibility(View.VISIBLE);
            finish();
            /*if (requestCode == CAMERA_REQUEST) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(photo);
            }*/
            //Toast.makeText(this, "Result Not Cancled", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mediaProjectionCallback = new MediaProjectionCallback();
                Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                        .setContentTitle("Recording")
                        .build();
                floatingViewService.startForeground(2, notification);
                //   floatingViewService.startForeground(NOTIFICATION_ID, notification);
                mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);


/*
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                }
*/
                mediaProjection.registerCallback(mediaProjectionCallback, null);

/*
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            }
*/
                virtualDisplay = createVirtualDisplay();

/*
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            }
*/
            }
            final boolean recordingWithoutCountDown = SPVariables.getString("CountDown", BlankActivity.this).equals("TRUE") ? true : false;
            if (recordingWithoutCountDown) {
                moveTaskToBack(true);
                new countdown().execute();
            } else {
                mediaRecorder.start();
                moveTaskToBack(true);
            }
            //mediaRecorder.start();
            //moveTaskToBack(true);
        }
       /* if (requestCode == REQUEST_CODE_PAUSE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mediaRecorder.pause();
            }
        }
        if (requestCode == REQUEST_CODE_PLAY) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mediaRecorder.resume();
            }

        }*/
    }




    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class MediaProjectionCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            if (toggleButton) {
                toggleButton = false;
                if(createVirtualDisplay() != null){
                    mediaRecorder.stop();
                }

                //mediaRecorder.stop();
                mediaRecorder.reset();
            }
            mediaProjection = null;
            stopRecordScreen();
            //super.onStop();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void stopRecordScreen() {
        if (virtualDisplay == null)
            return;
        virtualDisplay.release();
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            virtualDisplay.release();
        }*/
        destroyMediaProjetion();
        moveTaskToBack(false);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void destroyMediaProjetion() {
        if (mediaProjection != null) {
            mediaProjection.unregisterCallback(mediaProjectionCallback);
            mediaProjection.stop();

     /*       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mediaProjection.unregisterCallback(mediaProjectionCallback);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mediaProjection.stop();
            }
     */       mediaProjection = null;
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION: {
                if ((grantResults.length > 0) && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    //startOrStopRecording(toggleButton);
                    startRecording();
                } else {
                    toggleButton = false;
                    Snackbar.make(rootLayout, "Permissions", Snackbar.LENGTH_INDEFINITE)
                            .setAction("ENABLE", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ActivityCompat.requestPermissions(BlankActivity.this,
                                            new String[]{
                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                    Manifest.permission.RECORD_AUDIO
                                            }, REQUEST_PERMISSION);
                                }
                            }).show();
                }
                return;
            }
            case REQUEST_CODE_WRITE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Accepted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission denied 2", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public class countdown extends AsyncTask {
        private WindowManager wm;
        private LinearLayout ll;
        //private Button stop;
        private TextView textView;
        //private View v;

        public countdown() {
        }

        public countdown(View v) {
            //this.v = v;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            //startService(new Intent(BlankActivity.this, HoverSong.class));
            return "";
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onPostExecute(Object o) {
            try {
                if (toggleButton) {
                    //Toast.makeText(BlankActivity.this, "welcome", Toast.LENGTH_SHORT).show();
                    wm = (WindowManager) getSystemService(WINDOW_SERVICE);
                    ll = (LinearLayout) LayoutInflater.from(BlankActivity.this)
                            .inflate(R.layout.llcount, null);
                    textView = ll.findViewById(R.id.txtCount);

                    final WindowManager.LayoutParams parameters = new WindowManager.LayoutParams(
                            LinearLayout.LayoutParams.FILL_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            //WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                            //WindowManager.LayoutParams.TYPE_PHONE,
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                                    ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                                    : WindowManager.LayoutParams.TYPE_PHONE,
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                            PixelFormat.TRANSLUCENT);
                    parameters.x = 0;
                    parameters.y = 0;
                    parameters.gravity = Gravity.CENTER | Gravity.CENTER;
                    wm.addView(ll, parameters);
                    /*ll.setOnTouchListener(new View.OnTouchListener() {
                        private WindowManager.LayoutParams updatedParameters = parameters;
                        int x, y;
                        float touchedX, touchedY;

                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    x = updatedParameters.x;
                                    y = updatedParameters.y;
                                    touchedX = event.getRawX();
                                    touchedY = event.getRawY();
                                    break;
                                case MotionEvent.ACTION_MOVE:
                                    updatedParameters.x = (int) (x + (event.getRawX() - touchedX));
                                    updatedParameters.y = (int) (y + (event.getRawY() - touchedY));
                                    wm.updateViewLayout(ll, updatedParameters);
                                    //    break;
                                default:
                                    break;
                            }
                            return false;
                        }
                    });*/
    //                final boolean recordingWithoutCountDown = SPVariables.getString("CountDown", BlankActivity.this).equals("TRUE") ? true : false;
    //
    //                if (!recordingWithoutCountDown) {
    //                    mediaRecorder.start();
    //                }


                    /*new CountDownTimer(4000, 1000) {
                        public void onTick(long millisUntilFinished) {
                            //Toast.makeText(BlankActivity.this, millisUntilFinished+"", Toast.LENGTH_SHORT).show();
                            if (millisUntilFinished  > 999) {
                                long sec = millisUntilFinished / 1000;
                                textView.setText(millisUntilFinished / 1000 + "");
                                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
                                textView.startAnimation(animation);
                            } else {
                                *//*textView.setText("START");
                                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
                                textView.startAnimation(animation);*//*
                            }
                            //here you can have your logic to set text to edittext
                        }

                        public void onFinish() {
                            textView.setText("START");
                            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
                            textView.startAnimation(animation);
                            wm.removeView(ll);
    //                        if (recordingWithoutCountDown) {
    //                            mediaRecorder.start();
    //                        }
                            mediaRecorder.start();
                            //startOrStopRecording(v);
                        }
                    }.start();*/




                    new CountDownTimer(4000, 1000) {
                        public void onTick(long millisUntilFinished) {
                            if (millisUntilFinished/1000  > 0) {
                                long seconds = millisUntilFinished / 1000;
                                textView.setText(seconds+"");
                                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
                                textView.startAnimation(animation);
                            }
                            else {
                                textView.setText("START");
                                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
                                textView.startAnimation(animation);
                            }
                        }

                        public void onFinish() {
                            wm.removeView(ll);
                            try {
                                mediaRecorder.start();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e(TAG, e.getCause().toString());
                            }
                            //startOrStopRecording(v);
                        }
                    }.start();


                } else {
                    String aa = "";
                    //startOrStopRecording(v);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.onPostExecute(o);
        }
    }

    public class FloatingCam extends AsyncTask {
        //        public WindowManager wmCam;
//        public LinearLayout camPreivew;
        //private Button stop;
        private FrameLayout preview;
        private CameraView mPreview;

        //private View v;

        public FloatingCam() {
        }

        public FloatingCam(View v) {
            //this.v = v;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            //startService(new Intent(BlankActivity.this, HoverSong.class));
            return "";
        }


        @Override
        protected void onPostExecute(Object o) {
            if (toggleButton) {
                //Toast.makeText(BlankActivity.this, "welcome", Toast.LENGTH_SHORT).show();
                wmCam = (WindowManager) getSystemService(WINDOW_SERVICE);
                camPreivew = (LinearLayout) LayoutInflater.from(BlankActivity.this)
                        .inflate(R.layout.camera_preview, null);


                //preview = camPreivew.findViewById(R.id.txtCount);
                String IsFrontCam = SPVariables.getString("CameraFacing", BlankActivity.this);
                if (IsFrontCam.equals("Front")) {
                    mCamera = openFrontFacingCameraGingerbread();
                } else {
                    mCamera = getCameraInstance();
                }
                mPreview = new CameraView(BlankActivity.this, mCamera, wmCam);

                String CameraFrame = SPVariables.getString("CameraFrame", BlankActivity.this);
                preview = camPreivew.findViewById(R.id.camera_preview);
                preview.setBackgroundResource(R.drawable.rounded);


//                if (CameraFrame.equals("Square")) {
//
//                    preview.setBackgroundResource(R.drawable.rounded);
//                } else {
//                    //preview = camPreivew.findViewById(R.id.camera_preview_round);
//                    preview.setBackgroundResource(R.drawable.round);
//                }


                String ratio = SPVariables.getString("CameraPreview", BlankActivity.this);


                int CamPreviewWidth, CamPreviewHeight;
                if(ratio.equals("Large")){
                    CamPreviewWidth = 180;
                    CamPreviewHeight = 300;
                }
                else if(ratio.equals("Medium")){
                    CamPreviewWidth = 150;
                    CamPreviewHeight = 250;
                }
                else {
                    CamPreviewWidth = 120;
                    CamPreviewHeight = 200;
                }

                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)preview.getLayoutParams();
                layoutParams.height=dpToPx(CamPreviewHeight);
                layoutParams.weight=dpToPx(CamPreviewWidth);
                preview.setBackgroundResource(R.drawable.rounded);
                preview.setLayoutParams(layoutParams);

                //mPreview.setRotation(90.0f);
                preview.addView(mPreview);
                final WindowManager.LayoutParams parameters = new WindowManager.LayoutParams(
                        dpToPx(CamPreviewWidth),
                        dpToPx(CamPreviewHeight),
                        //WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                                : WindowManager.LayoutParams.TYPE_PHONE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);

                /*Build.VERSION.SDK_INT <= Build.VERSION_CODES.R
                        ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                        : WindowManager.LayoutParams.TYPE_PHONE,*/
                parameters.x = 0;
                parameters.y = 0;
                //parameters.gravity = Gravity.RIGHT | Gravity.TOP;
                wmCam.addView(camPreivew, parameters);

//                int width = display.getWidth();
//                int height = display.getHeight();

                camPreivew.setOnTouchListener(new View.OnTouchListener() {
                    private WindowManager.LayoutParams updatedParameters = parameters;
                    int x, y;
                    float touchedX, touchedY;

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                x = updatedParameters.x;
                                y = updatedParameters.y;
                                touchedX = event.getRawX();
                                touchedY = event.getRawY();
                                break;
                            case MotionEvent.ACTION_MOVE:
                                updatedParameters.x = (int) (x + (event.getRawX() - touchedX));
                                updatedParameters.y = (int) (y + (event.getRawY() - touchedY));
                                wmCam.updateViewLayout(camPreivew, updatedParameters);
                                //    break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
            } else {
                String aa = "";
                //startOrStopRecording(v);
            }
            super.onPostExecute(o);
        }
    }

    private Camera openFrontFacingCameraGingerbread() {
        int cameraCount = 0;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    cam = Camera.open(camIdx);
                } catch (RuntimeException e) {
                    Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
                }
            }
        }
        return cam;
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void hearShake() {

        floatingViewService.ShowNotification("Stop");

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        }*/
        try {
            stopRecording();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mFloatingView.findViewById(R.id.collapse_view).setVisibility(View.VISIBLE);
        mFloatingView.findViewById(R.id.collapse_view_stop).setVisibility(View.GONE);
        mFloatingView.findViewById(R.id.close_btn).setVisibility(View.VISIBLE);


//        mediaRecorder.stop();
//        mediaRecorder.reset();
//        toggleButton = false;
//        stopRecordScreen();
        //Play in video view
        //videoView.setVisibility(View.VISIBLE);
        //videoView.setVideoURI(Uri.parse(videoUri));
        //videoView.start();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        String RecordwithCam = getIntent().getStringExtra("RecordWithCamera") == null ? "NO" : getIntent().getStringExtra("RecordWithCamera");
        if (RecordwithCam.equals("YES")) {
            if (mCamera!=null) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
                wmCam.removeView(camPreivew);
            }
        }
        SPVariables.setString("RecordStartOrStop", "NOTSTARTED", getApplicationContext());
        destroyMediaProjetion();
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteDir(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        return dir.delete();
    }


}
