package com.manddprojectconsultant.screencam.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.manddprojectconsultant.screencam.R;
public class VideoPlayerActivity extends AppCompatActivity {

    VideoView VideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        VideoView =  findViewById(R.id.VideoView);

        String VideoPath = getIntent().getStringExtra("VideoPath");
        String VideoFileName = getIntent().getStringExtra("VidelFileName");

        setTitle(VideoFileName);

        Uri uri = Uri.parse(VideoPath);
//        Uri uri = Uri.parse(Environment.getExternalStorageDirectory()+"/video000/SRVideo.mp4");

        //Uri uri = FileProvider.getUriForFile(VideoPlayerActivity.this, getPackageName(), new File(VideoPath));

        VideoView.setVideoURI(uri);

        MediaController mediaController = new MediaController(this);
        VideoView.setMediaController(mediaController);
        VideoView.start();

        mediaController.setAnchorView(VideoView);
        //Toast.makeText(this, "File Paht: "+VideoPath, Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_video_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_edit_video) {
            Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
