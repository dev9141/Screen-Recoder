package com.manddprojectconsultant.screencam.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.manddprojectconsultant.screencam.GooglePlayStoreAppVersionNameLoader;
import com.manddprojectconsultant.screencam.utils.PreferenceManager;
import com.manddprojectconsultant.screencam.R;
import com.manddprojectconsultant.screencam.adapter.SliderAdapter;
public class WelcomeActivity extends AppCompatActivity {


    ViewPager viewPager;
    LinearLayout dotsLayout;
    SliderAdapter sliderAdapter;
    TextView[] dots;
    Button letsGetStarted,btnskip;
     ImageView btnnext;
    Animation animation;
    int currentPos;
    TextView tvmadeinindia;
    
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);

        preferenceManager = new PreferenceManager(this);
        if (!preferenceManager.isFirstTimeLaunch()) {
            //Call adapter
            restorePrefData();
            finish();
        }

        init();
        sliderAdapter = new SliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);
        
        //Dots
        addDots(0);
        viewPager.addOnPageChangeListener(changeListener);
    }

    private void init() {

        viewPager = findViewById(R.id.slider);
        dotsLayout = findViewById(R.id.dots);
        letsGetStarted = findViewById(R.id.get_started_btn);
        btnskip=findViewById(R.id.btn_skip);
        btnnext=findViewById(R.id.btnnext);
        tvmadeinindia=findViewById(R.id.tvmadeinindia);

    }
    private void  restorePrefData() {
        preferenceManager.setFirstTimeLaunch(false);
        startActivity(new Intent(WelcomeActivity.this, DashboardActivity.class));
        finish();
    }

    private void addDots(int position) {
        dots = new TextView[3];
        dotsLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.videoListStrip));
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(R.color.coloryellow));
        }

    }
    
    ViewPager.OnPageChangeListener changeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            addDots(position);
            currentPos = position;

            if (position == 0) {
                letsGetStarted.setVisibility(View.INVISIBLE);
                tvmadeinindia.setVisibility(View.VISIBLE);
            } else if (position == 1) {
                letsGetStarted.setVisibility(View.INVISIBLE);
                tvmadeinindia.setVisibility(View.GONE);

            } else {
                animation = AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.button_animation);
                letsGetStarted.setAnimation(animation);
                letsGetStarted.setVisibility(View.VISIBLE);
                tvmadeinindia.setVisibility(View.GONE);
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    
    public void skip(View view) {
        startActivity(new Intent(this, DashboardActivity.class));
        preferenceManager.setFirstTimeLaunch(false);
        new GooglePlayStoreAppVersionNameLoader().execute();
        finish();
    }

    public void next(View view) {
        if(letsGetStarted.getVisibility() == View.INVISIBLE){
            viewPager.setCurrentItem(currentPos + 1);
        }
        else{
            letgo(view);
        }
    }

    public void letgo(View view) {
        startActivity(new Intent(this, DashboardActivity.class));
        preferenceManager.setFirstTimeLaunch(false);
        new GooglePlayStoreAppVersionNameLoader().execute();
        finish();
    }
}