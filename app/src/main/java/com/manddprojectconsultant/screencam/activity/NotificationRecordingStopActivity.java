package com.manddprojectconsultant.screencam.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import static com.manddprojectconsultant.screencam.service.FloatingViewService.mFloatingView;

import com.manddprojectconsultant.screencam.service.FloatingViewService;
import com.manddprojectconsultant.screencam.R;
import com.manddprojectconsultant.screencam.utils.SPVariables;

import java.io.IOException;
public class NotificationRecordingStopActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    public static BlankActivity blankActivity;
    @SuppressLint("StaticFieldLeak")
    public static FloatingViewService floatingViewService;

    LinearLayout rootNotiStop;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_recording_stop);

        rootNotiStop = findViewById(R.id.rootNotiStop);
        rootNotiStop.setBackgroundColor(getResources().getColor(R.color.transpermt));

        SPVariables.setString("RecordStartOrStop", "NOTSTARTED", getApplicationContext());
        try {
            blankActivity.stopRecording();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String RecordwithCam = getIntent().hasExtra("RecordWithCamera")?"YES":"NO";
        if (RecordwithCam.equals("YES")) {
            blankActivity.mCamera.stopPreview();
            blankActivity.mCamera.release();
            blankActivity.mCamera = null;
            blankActivity.wmCam.removeView(blankActivity.camPreivew);
        }

        mFloatingView.findViewById(R.id.collapse_view).setVisibility(View.VISIBLE);
        mFloatingView.findViewById(R.id.close_btn).setVisibility(View.VISIBLE);
        mFloatingView.findViewById(R.id.collapse_view_stop).setVisibility(View.GONE);

        floatingViewService.ShowNotification("Stop");

        finish();
    }
}
