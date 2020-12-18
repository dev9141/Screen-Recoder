package com.manddprojectconsultant.screenrecorder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
public class GridLayoutActivity extends AppCompatActivity {

    ImageView ivlistnormallist,ivsettings;
    RecyclerView rvVideoListforgridview;
    public static final int RequestPermissionCode = 7;
    boolean firstStart;
    ArrayList<VideoModel> lstVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_layout);
        lstVideo = new ArrayList<>();
        init();




        ivsettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent setting=new Intent(GridLayoutActivity.this,SettingActivity.class);
                setting.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(setting);
                overridePendingTransition(0,0);

            }
        });


    }


    private void init() {


        ivlistnormallist=findViewById(R.id.ivlistnormallist);
        ivsettings=findViewById(R.id.ivsettings);
        rvVideoListforgridview=findViewById(R.id.rvVideoListforgridview);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),2);
        rvVideoListforgridview.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView
        //  call the constructor of CustomAdapter to send the reference and data to Adapter
        VideoListforgridadapter videoListforgridadapter = new VideoListforgridadapter(lstVideo,this);
        rvVideoListforgridview.setAdapter(videoListforgridadapter);
    }


    public void ListClickforDashboard(View view) {
        Intent normal=new Intent(GridLayoutActivity.this,MainActivity.class);
        normal.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(normal);
        overridePendingTransition(0,0);


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


}