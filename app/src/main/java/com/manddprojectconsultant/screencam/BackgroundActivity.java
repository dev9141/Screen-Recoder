package com.manddprojectconsultant.screencam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import static com.manddprojectconsultant.screencam.FloatingViewService.mFloatingViewExpand;
public class BackgroundActivity extends AppCompatActivity {

    public Context context = BackgroundActivity.this;
    public static FloatingViewService floatingViewService;
    public static RelativeLayout rootBGView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background);
        //NotiRecordingStart.backgroundActivity = this;

        rootBGView = findViewById(R.id.rootBGView);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View expandedView = mFloatingViewExpand.findViewById(R.id.expanded_container);
        if(expandedView.getVisibility() == View.GONE) {
            this.finish();
        }



        rootBGView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingViewService.expandedView.setVisibility(View.GONE);
                rootBGView.setBackgroundColor(getResources().getColor(R.color.transpermt));
                finish();
            }
        });

        FloatingViewService.backgroundActivity = this;
        //Toast.makeText(this, "Hello Its BG Activity", Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        floatingViewService.expandedView.setVisibility(View.GONE);
        rootBGView.setBackgroundColor(getResources().getColor(R.color.transpermt));

    }
}
