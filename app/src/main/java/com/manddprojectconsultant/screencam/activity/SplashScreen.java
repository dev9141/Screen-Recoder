package com.manddprojectconsultant.screencam.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import com.manddprojectconsultant.screencam.utils.SPVariables;
public class SplashScreen extends AppCompatActivity {
    public static final int RequestPermissionCode = 7;
    public static final String PREF_KEY_FIRST_START = "PREF_KEY_FIRST_START";

    ImageView ivsplashlogo;
    Animation topanim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //setContentView(R.layout.activity_splash_screen);
        //Checking App Permission Granted or Not
        if (CheckingPermissionIsEnabledOrNot()) {
            //Entered in if Permission is granted
            //Toast.makeText(ActivityLogin.this, "All Permissions Granted Successfully", Toast.LENGTH_SHORT).show();
            //setAllSP if SP is empty
            setSP(SplashScreen.this);
            init();



            /**
             *
             * 0 = After Install First time open or Clear All Data from Setting, so open bubble and home activity both
             * 1 = After Install it's not First time open, so open direct bubble only
             *
             * **/
            //File file = new File(Environment.getExternalStorageDirectory(),"ScreenRecord");
            File file = new File(Environment.getExternalStorageDirectory(), "video000");
            File videoFile = new File(file, "SRVideo.mp4");
            if (file.exists()) {
                //Toast.makeText(this, "exist", Toast.LENGTH_SHORT).show();
            } else {
                file.mkdir();
                //Toast.makeText(this, "not exist", Toast.LENGTH_SHORT).show();
            }

            /*if(videoFile.exists()){
                Toast.makeText(this, "exist", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "not exist", Toast.LENGTH_SHORT).show();
            }*/
           Animationhandling();
        }
        // If, If permission is not enabled then else condition will execute.
        else {
            //Calling method to enable permission.
            RequestMultiplePermission();
        }
    }

    private void init() {

        //ivsplashlogo=findViewById(R.id.ivsplashlogo);
        /*topanim = AnimationUtils.loadAnimation(this, R.anim.animation);
        ivsplashlogo.setAnimation(topanim);*/

    }

    private void setSP(Context context) {
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
        if (SPVariables.getString("Camera", context) == null || SPVariables.getString("Camera", context).isEmpty()) {
            SPVariables.setString("Camera", "Front", context);
        }
        if (SPVariables.getString("RecordAudio", context) == null || SPVariables.getString("RecordAudio", context).isEmpty()) {
            SPVariables.setString("RecordAudio", "TRUE", context);
        }
        if (SPVariables.getString("CountDown", context) == null || SPVariables.getString("CountDown", context).isEmpty()) {
            SPVariables.setString("CountDown", "TRUE", context);
        }
        if (SPVariables.getString("AppIntro", context) == null || SPVariables.getString("AppIntro", context).isEmpty()) {
            SPVariables.setString("AppIntro", "TRUE", context);
        }
        if (SPVariables.getString("ShowBubble", context) == null || SPVariables.getString("ShowBubble", context).isEmpty()) {
            SPVariables.setString("ShowBubble", "TRUE", context);
        }
        if (SPVariables.getString("RecordStartOrStop", context) == null || SPVariables.getString("RecordStartOrStop", context).isEmpty()) {
            SPVariables.setString("RecordStartOrStop", "NOTSTARTED", context);
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
        ActivityCompat.requestPermissions(SplashScreen.this, new String[]
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
                        init();
                        Animationhandling();
                    } else {
                        finish();
                        Toast.makeText(SplashScreen.this, "Permission Denied 4", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private void Animationhandling() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(SplashScreen.this, DashboardActivity.class);
                // intent.putExtra(MainActivity.EXTRA_OBJECT, object);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(SplashScreen.this,ivsplashlogo,"imagelogo");
                startActivity(intent, options.toBundle());
            }
        }, 4000);



    }
}



/*
public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_splash_screen);

        Intent i = new Intent(this, IntroActivity.class);
        startActivity(i);
        finish();
    }
}
*/
