package com.manddprojectconsultant.screencam;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.TransitionSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
public class Checkingforbutton extends AppCompatActivity {
    Button btnanimation;
    TextView tvanim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkingforbutton);
        btnanimation = findViewById(R.id.btnanimation);
        tvanim = findViewById(R.id.tvanim);
        btnanimation.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void onClick(View view) {

                TransitionSet set = new TransitionSet();
                set.setOrdering(TransitionSet.MATCH_INSTANCE);
                set.setDuration(100);
                set.addTransition(new Fade());
                set.addTarget(tvanim);
                tvanim.setVisibility(View.VISIBLE);
                //TransitionManager.beginDelayedTransition((ViewGroup) view,set);
            }
        });
    }
}