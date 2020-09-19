package com.example.screenrecorder;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import static android.content.ContentValues.TAG;

public class CamService extends Service {

    public static WindowManager wm;
    public static LinearLayout camPreivew;

    //private Button stop;
    private FrameLayout preview;

    public static Camera mCamera;
    private CameraPreview mPreview;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        super.onCreate();


        wm = (WindowManager) getSystemService(WINDOW_SERVICE);


        camPreivew = (LinearLayout) LayoutInflater.from(this)
                .inflate(R.layout.camera_preview, null);
        preview = camPreivew.findViewById(R.id.txtCount);


        //mCamera = openFrontFacingCameraGingerbread();
        mCamera = getCameraInstance();
        //mCamera = getCameraInstance();
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera, wm);
        preview = camPreivew.findViewById(R.id.camera_preview);
        //mPreview.setRotation(90.0f);
        preview.addView(mPreview);



              /*  camPreivew = new LinearLayout(MainActivity.this);
                //stop = new Button(this);
                preview = new TextView(MainActivity.this);

                ViewGroup.LayoutParams btnParameters = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                //stop.setText("Stop");
                //stop.setLayoutParams(btnParameters);

                preview.setLayoutParams(btnParameters);
                preview.setText("This is a sample TextView...");
                preview.setTextColor(Color.parseColor("#ff0000"));


                LinearLayout.LayoutParams llParameters = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                camPreivew.setBackgroundColor(Color.argb(66, 255, 0, 0));
                camPreivew.setLayoutParams(llParameters);



                //camPreivew.addView(stop);
                camPreivew.addView(preview);*/
        final WindowManager.LayoutParams parameters = new WindowManager.LayoutParams(dpToPx(180), dpToPx(240), WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        parameters.x = 0;
        parameters.y = 0;
        //parameters.gravity = Gravity.RIGHT | Gravity.TOP;
        wm.addView(camPreivew, parameters);

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

                        wm.updateViewLayout(camPreivew, updatedParameters);
                        //    break;

                    default:
                        break;
                }


                return false;
            }
        });

    }

    /*public static void removeCamView(){
        mCamera.stopPreview();
        wm.removeView(camPreivew);
    }*/

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

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    public int pxToDp(int px) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
