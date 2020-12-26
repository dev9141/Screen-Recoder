package com.manddprojectconsultant.screenrecorder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
public class Welcome_Screen extends AppCompatActivity {


    ViewPager viewPager;
    LinearLayout dotsLayout;
    SliderAdapter sliderAdapter;
    TextView[] dots;
    Button letsGetStarted,btnskip;
     ImageView btnnext;
    Animation animation;
    int currentPos;

    PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome__screen);





        // when this activity is about to be launch we need to check if its openened before or not





        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {

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

    }



        private void  restorePrefData() {




            prefManager.setFirstTimeLaunch(false);
            startActivity(new Intent(Welcome_Screen.this, MainActivity.class));
            finish();





        }

    private void addDots(int position) {

        dots = new TextView[3];
        dotsLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("•"));
            dots[i].setTextSize(35);

            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(R.color.coloryellow));
        }

    }




    /*private void initforWELCOMESCREEN() {



        btnnext=findViewById(R.id.btnnext);
        btnskip=findViewById(R.id.btn_skip);

    }
*/
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
            } else if (position == 1) {
                letsGetStarted.setVisibility(View.INVISIBLE);

            } else {
                animation = AnimationUtils.loadAnimation(Welcome_Screen.this, R.anim.button_animation);
                letsGetStarted.setAnimation(animation);
                letsGetStarted.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };



    public void skip(View view) {
        startActivity(new Intent(this, MainActivity.class));
        prefManager.setFirstTimeLaunch(false);
        finish();
    }

    public void next(View view) {
        viewPager.setCurrentItem(currentPos + 1);
    }

    public void letgo(View view) {
        startActivity(new Intent(this, MainActivity.class));
        prefManager.setFirstTimeLaunch(false);
        finish();


    }
}