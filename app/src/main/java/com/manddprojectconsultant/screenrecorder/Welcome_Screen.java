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
    /*private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnNext;
    ImageView btnSkip;
    private PrefManager prefManager;*/


    private ViewPager screenPager;
    MyViewPagerAdapter myViewPagerAdapter ;
    TabLayout tabIndicator;
    Button btnNext;
    int position = 0 ;
    Button btnGetStarted;
    Animation btnAnim ;
    TextView tvSkip;
    PrefManager prefManager;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);



        // when this activity is about to be launch we need to check if its openened before or not

        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {

            restorePrefData();
            finish();

        }

        setContentView(R.layout.activity_welcome__screen);

//        getSupportActionBar().hide();

        initforscreenrecoder();



        final List<ScreenItem> mList = new ArrayList<>();
        mList.add(new ScreenItem("DashBoard","How to use Dashboard,it show the process on this video",R.drawable.settingsvideo));
        mList.add(new ScreenItem("Video + Screen Record","How to use Video + Screen Recorder,it show the process on this video",R.drawable.videorecoder));
        mList.add(new ScreenItem("Screen Record","How to use Screen Recorder,it show the process on this video",R.drawable.screenshortforrecosing));

        // setup viewpager
        screenPager =findViewById(R.id.screen_viewpager);
        myViewPagerAdapter   = new MyViewPagerAdapter(Welcome_Screen.this,mList);
        screenPager.setAdapter(myViewPagerAdapter);

        // setup tablayout with viewpager

        tabIndicator.setupWithViewPager(screenPager);

        // next button click Listner

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                position = screenPager.getCurrentItem();
                if (position < mList.size()) {

                    position++;
                    screenPager.setCurrentItem(position);


                }

                if (position == mList.size()-1) { // when we rech to the last screen



                    loaddLastScreen();


                }



            }
        });



        tabIndicator.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == mList.size()-1) {

                    loaddLastScreen();

                }


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



        // Get Started button click listener

        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //open main activity

                Intent mainActivity = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(mainActivity);
                prefManager.setFirstTimeLaunch(false);
                finish();



            }
        });

        // skip button click listener

        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screenPager.setCurrentItem(mList.size());
            }
        });






       /* // Checking for first time launch - before calling setContentView()
        prefManager = new PrefManager(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        initforWelcomeScreen();


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int current=getItem(+1);
                if (current<layouts.length)
                {
                    viewPager.setCurrentItem(current);
                }else{
                    launchHomeScreen();
                }

            }
        });


        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int current=getItem(-1);
                if (current<layouts.length)
                {
                    viewPager.setCurrentItem(current);
                }else{
                    launchHomeScreen();
                }
            }
        });



        layouts = new int[]{
                R.layout.welcome_page_1,
                R.layout.welcome_page_2,
                R.layout.welcome_page_3,
                R.layout.welcome_page_4};

        addBottomDots(0);

        changeStatusBarColor();

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        //   viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                addBottomDots(position);
                // changing the next button text 'NEXT' / 'GOT IT'
                if (position == layouts.length - 1) {
                    // last page. make button text to GOT IT
                    btnNext.setText(getString(R.string.start));
                    btnSkip.setVisibility(View.GONE);
                } else {
                    // still pages are left
                    btnNext.setText(getString(R.string.next));
                    btnSkip.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];
        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);
        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }
        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void initforWelcomeScreen() {
        viewPager = findViewById(R.id.welcomepager);
        dotsLayout = findViewById(R.id.layoutdots);
        btnSkip = findViewById(R.id.btnskip);
        btnNext = findViewById(R.id.btnnrext);
    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(Welcome_Screen.this, MainActivity.class));
        finish();
    }

    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
*/
    }




    private void initforscreenrecoder() {


        btnNext=findViewById(R.id.btn_next);
        btnGetStarted=findViewById(R.id.btn_get_started);
        tabIndicator=findViewById(R.id.tab_indicator);
        btnAnim= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_animation);
        tvSkip=findViewById(R.id.tv_skip);


    }

    private void  restorePrefData() {




        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(Welcome_Screen.this, MainActivity.class));
        finish();


        }




    private void loaddLastScreen() {

        btnNext.setVisibility(View.INVISIBLE);
        btnGetStarted.setVisibility(View.VISIBLE);
        tvSkip.setVisibility(View.INVISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);
        btnGetStarted.setAnimation(btnAnim);



    }



}