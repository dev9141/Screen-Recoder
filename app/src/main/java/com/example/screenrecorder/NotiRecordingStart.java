package com.example.screenrecorder;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.example.screenrecorder.FloatingViewService.expandedView;
import static com.example.screenrecorder.FloatingViewService.mFloatingView;
public class NotiRecordingStart extends AppCompatActivity {

    public static BlankActivity blankActivity;
    public static BackgroundActivity backgroundActivity = new BackgroundActivity();
    public static FloatingViewService floatingViewService;

    LinearLayout rootNotiStart;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noti_recording_start);

        startActivity(new Intent(NotiRecordingStart.this, MainActivity.class)
                .putExtra("OpenHome", true)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_ANIMATION));
        backgroundActivity.finish();
        expandedView.setVisibility(View.GONE);

        /*rootNotiStart = findViewById(R.id.rootNotiStart);
        rootNotiStart.setBackgroundColor(getResources().getColor(R.color.transpermt));

        SPVariables.setString("RecordStartOrStop", "STARTED", getApplicationContext());

        mFloatingView.findViewById(R.id.collapse_view_stop).setVisibility(View.VISIBLE);
        mFloatingView.findViewById(R.id.collapse_view).setVisibility(View.GONE);
        backgroundActivity.finish();

        expandedView.setVisibility(View.GONE);

        floatingViewService.ShowNotification("Start");
        String bubbleShow = SPVariables.getString("ShowBubble", getApplicationContext());
        if(bubbleShow.equals("FALSE")){
            mFloatingView.findViewById(R.id.collapse_view_stop).setVisibility(View.GONE);
            mFloatingView.findViewById(R.id.collapse_view).setVisibility(View.GONE);
            mFloatingView.findViewById(R.id.close_btn).setVisibility(View.GONE);

        }

        Intent intent = new Intent(getApplicationContext(), BlankActivity.class);
        intent.putExtra("Record", "START");
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();*/

    }
}
