package com.manddprojectconsultant.screencam;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.icu.number.Scale;
import android.os.Build;
import android.os.IBinder;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.transition.Visibility;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.interpolator.view.animation.FastOutLinearInInterpolator;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;

import java.util.Random;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class FloatingViewService extends Service {

    private WindowManager mWindowManager;
    public static View mFloatingView;
    public static View mFloatingViewExpand;
    private static final String CHANNEL_ID = "channel_id01";
    public static final int NOTIFICATION_ID = 1;
    public static final int NOTIFICATION_ID_2 = 2;


    private int isExpand = 0; //0 = not expand, 1 = expanded


    private GestureDetector gestureDetector;
    public static int recordFlag = 0;
    WindowManager.LayoutParams Rparams;

    public static BlankActivity blankActivity;
    public static BackgroundActivity backgroundActivity = new BackgroundActivity();

    public static View expandedView;

    public RelativeLayout recordView;

    public static MainActivity mainActivity;

    public String widget = "Big";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();

        BackgroundActivity.floatingViewService = this;
        BlankActivity.floatingViewService = this;
        NotiRecordingStop.floatingViewService = this;
        NotiRecordingStart.floatingViewService = this;

        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);
        mFloatingViewExpand = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget_expand, null);

        gestureDetector = new GestureDetector(this, new SingleTapConfirm());
        gestureDetector.setIsLongpressEnabled(true);

        backgroundActivity = (BackgroundActivity) backgroundActivity.context;

        recordView = mFloatingView.findViewById(R.id.recordView);

        CreateNotificationChannel();
        ShowNotification("");

        //Add the view to the window.
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                //WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                //WindowManager.LayoutParams.TYPE_PHONE,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                        ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                        : WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);


        /*final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);*/

        //Specify the view position
        //params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
        params.gravity = Gravity.CENTER | Gravity.RIGHT ;        //Initially view will be added to center-right
//        params.x = 600;
//        params.y = 0;
        Log.e("Widget Position: ", "Default x: "+params.x);
        Log.e("Widget Position: ", "Default y: "+params.y);

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingViewExpand, params);
        mWindowManager.addView(mFloatingView, params);

        try {
            Display display = mWindowManager.getDefaultDisplay();
            int width = display.getWidth();
            int height = display.getHeight();
            Log.e("Widget Height:", " "+height);
            Log.e("Widget width:", ""+width);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Rparams = params;

        //new countdown(getApplicationContext()).execute();

        //The root element of the collapsed view layout
        //final View collapsedView = mFloatingView.findViewById(R.id.collapse_view);
        //The root element of the expanded view layout
        expandedView = mFloatingViewExpand.findViewById(R.id.expanded_container);
        //final View expandedViewClose = mFloatingView.findViewById(R.id.expanded_container_close);

        if(expandedView.getVisibility() == View.GONE) {
            //expandedView.setVisibility(View.VISIBLE);
            Intent intent = new Intent(getApplicationContext(), BackgroundActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            backgroundActivity.finish();
        }
        //Set the close button
        ImageView closeButtonCollapsed = (ImageView) mFloatingView.findViewById(R.id.close_btn);
        closeButtonCollapsed.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                //close the service and remove the from from the window
                if (expandedView.getVisibility() == View.VISIBLE) {
                    backgroundActivity.finish();
                }
                stopSelf();
                closeNotification();

                //blankActivity.finishAffinity();
                //backgroundActivity.killSelf();
                backgroundActivity.finishAndRemoveTask();
                System.exit(0);


                /*int pid = android.os.Process.myPid();
                android.os.Process.killProcess(pid);*/

            }
        });

        mFloatingView.findViewById(R.id.collapse_view).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;






            @Override
            public boolean onTouch(View v, MotionEvent event) {



                TransitionSet set = new TransitionSet();
                set.setOrdering(TransitionSet.MATCH_INSTANCE);
                set.setDuration(100);
                set.addTransition(new Fade());
                set.addTarget(v);





                if (gestureDetector.onTouchEvent(event)) {





                    int Xdiff = (int) (event.getRawX() - initialTouchX);
                    int Ydiff = (int) (event.getRawY() - initialTouchY);

                    //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                    //So that is click event.
                    if (Xdiff < 10 && Ydiff < 10) {
                        //if (isViewCollapsed()) {
                        //if (isExpand == 0) {
                        if (expandedView.getVisibility() == View.GONE) {
                            //When user clicks on the image view of the collapsed layout,
                            //visibility of the collapsed layout will be changed to "View.GONE"
                            //and expanded view will become visible.
                            //collapsedView.setVisibility(View.GONE);
                            isExpand = 1;

//                            FrameLayout.LayoutParams params =
//                                    (FrameLayout.LayoutParams)recordView.getLayoutParams();
//                            params.setMargins(dpToPx(recordView, 75), dpToPx(recordView, 61), 0, 0);
//                            recordView.setLayoutParams(params);

                            expandedView.setVisibility(View.VISIBLE);

                            Intent intent = new Intent(getApplicationContext(), BackgroundActivity.class);
                            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_ANIMATION);

                            // intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                            backgroundActivity.finish();

                        } else {
                            isExpand = 0;
                            expandedView.setVisibility(View.GONE);
//                            FrameLayout.LayoutParams params =
//                                    (Fra  meLayout.LayoutParams)recordView.getLayoutParams();
//                            params.setMargins(0, 0, 0, 0);
//                            recordView.setLayoutParams(params);
//                            backgroundActivity.rootBGView.setBackgroundColor(getResources().getColor(R.color.transpermt));
                            backgroundActivity.finish();
                        }
                    }
                    return true;
                } else {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:

                            //remember the initial position.
                            initialX = params.x;
                            initialY = params.y;

                            Log.e("Action Down initialY: ", initialY+"");

                            //get the touch location
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            return true;

                        case MotionEvent.ACTION_BUTTON_PRESS:
                            //Toast.makeText(FloatingViewService.this, "hello", Toast.LENGTH_SHORT).show();
                        case MotionEvent.ACTION_MOVE:

                            int Xdiff = (int) (event.getRawX() - initialTouchX);
                            int Ydiff = (int) (event.getRawY() - initialTouchY);
                            //Log.e("Wegdet event Y", "onTouch: "+ event.getRawY());


                            if (Xdiff != 0 && Ydiff != 0) {
                                if (expandedView.getVisibility() == View.VISIBLE) {
                                    //if (isExpand == 1) {
                                    //When user clicks on the image view of the collapsed layout,
                                    //visibility of the collapsed layout will be changed to "View.GONE"
                                    //and expanded view will become visible.
                                    //collapsedView.setVisibility(View.GONE);
                                    isExpand = 0;
                                    backgroundActivity.finish();
                                    expandedView.setVisibility(View.GONE);
                                }
                            }

                            //Calculate the X and Y coordinates of the view.
                            //params.x = initialX + (int) (event.getRawX() - initialTouchX);

                            Display display = mWindowManager.getDefaultDisplay();
                            int width = display.getWidth();
                            int height = display.getHeight();
                            height=((90*height)/100);
                            Log.e("Widget Height:", " "+height);
                            //Log.e("Widget width:", ""+width);

                            int diffheight = height-params.y;

                            int heightU = mainActivity.heightU;
                            int heightB = mainActivity.heightB;


                            //Log.e("Widget Y diff", "onTouch: "+ (height - event.getRawY()));
//                            if(event.getRawY()<=1600 && event.getRawY()>=420) {
                            //if((height - event.getRawY())>=280 && (height - event.getRawY())<=1600) {
                            //if(heightU>=500 && heightB <=2032) {
                            if(event.getRawY()>= heightU && event.getRawY()<=(mainActivity.height<1200?heightB-50:heightB)) {
                                params.y = initialY + (int) (event.getRawY() - initialTouchY);
                                Log.e("initialY: ", initialY+"");
                                Log.e("Y ", initialY+ " " + event.getRawY() +"-"+ initialTouchY);
                                // Log.e("Widget Position: ", "before update x: "+params.x);
                                //Log.e("Widget Position: ", "before update y: "+params.y);
                                //Update the layout with new X & Y coordinate
                                mWindowManager.updateViewLayout(mFloatingViewExpand, params);
                                mWindowManager.updateViewLayout(mFloatingView, params);
                                Rparams = params;
                                //Log.e("Widget Position: ", "after update x: "+Rparams.x);
                                //Log.e("Widget Position: ", "after update y: "+Rparams.y);
                            }
                            return true;
                    }
                }

                return false;
            }

        });



        mFloatingView.findViewById(R.id.collapse_view).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //if (isExpand == 0) {
                if (expandedView.getVisibility() == View.VISIBLE) {
                    backgroundActivity.finish();
                    expandedView.setVisibility(View.GONE);

                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mFloatingView.findViewById(R.id.collapse_view).getLayoutParams();
                    params.setMargins(0, dpToPx(mFloatingView.findViewById(R.id.collapse_view), 60), 0, 0);
                    mFloatingView.findViewById(R.id.collapse_view).setLayoutParams(params);
                    //expandedViewClose.setVisibility(View.VISIBLE);
                    //close_iv.setVisibility(View.GONE);
                }
                return false;
            }
        });

        /*mFloatingView.findViewById(R.id.collapse_view_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPVariables.setString("RecordStartOrStop", "NOTSTARTED", getApplicationContext());
                mFloatingView.findViewById(R.id.collapse_view).setVisibility(View.VISIBLE);
                mFloatingView.findViewById(R.id.collapse_view_stop).setVisibility(View.GONE);
            }
        });*/


        mFloatingView.findViewById(R.id.collapse_view_stop).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (gestureDetector.onTouchEvent(event)) {

                    //int Xdiff = (int) (event.getRawX() - initialTouchX);
                    int Ydiff = (int) (event.getRawY() - initialTouchY);

                    //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                    //So that is click event.
                    if (/*Xdiff < 10 && */Ydiff < 10) {
                        //if (isViewCollapsed()) {
                        if (SPVariables.getString("RecordStartOrStop", getApplicationContext()).equals("STARTED")) {
                            //When user clicks on the image view of the collapsed layout,
                            //visibility of the collapsed layout will be changed to "View.GONE"
                            //and expanded view will become visible.
                            //collapsedView.setVisibility(View.GONE);

                            SPVariables.setString("RecordStartOrStop", "NOTSTARTED", getApplicationContext());

//                            Intent intent = new Intent(getApplicationContext(), BlankActivity.class);
//                            intent.putExtra("Record2", "STOP");
//                            intent.putExtra("Record", "STOP");
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);

                            blankActivity.stopRecording();
                            ShowNotification("Stop");
                            //CamService.removeCamView();
                            //blankActivity.stopRecordingWithCam();

                            mFloatingView.findViewById(R.id.collapse_view).setVisibility(View.VISIBLE);
                            mFloatingView.findViewById(R.id.collapse_view_stop).setVisibility(View.GONE);
                            mFloatingView.findViewById(R.id.close_btn).setVisibility(View.VISIBLE);





                        }
                    }
                    return true;
                } else {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:

                            //remember the initial position.
                            //initialX = params.x;
                            initialY = params.y;

                            //get the touch location
                            //initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            return true;

                        case MotionEvent.ACTION_BUTTON_PRESS:
                            //Toast.makeText(FloatingViewService.this, "hello", Toast.LENGTH_SHORT).show();
                        case MotionEvent.ACTION_MOVE:
                            //Calculate the X and Y coordinates of the view.
                            //params.x = initialX + (int) (event.getRawX() - initialTouchX);
                            params.y = initialY + (int) (event.getRawY() - initialTouchY);

                            //Update the layout with new X & Y coordinate
                            mWindowManager.updateViewLayout(mFloatingViewExpand, params);
                            mWindowManager.updateViewLayout(mFloatingView, params);

                            Rparams = params;
                            return true;
                    }
                }

                return false;
            }

        });


        mFloatingViewExpand.findViewById(R.id.iv_Record).setOnClickListener(new View.OnClickListener() {

            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                //Toast.makeText(FloatingViewService.this, SPVariables.getString("RecordStartOrStop", getApplicationContext()), Toast.LENGTH_SHORT).show();

//              params.x = 600;
//              params.y = 0;
                mWindowManager.updateViewLayout(mFloatingViewExpand, params);
                mWindowManager.updateViewLayout(mFloatingView, params);
                Rparams = params;

                if (SPVariables.getString("RecordStartOrStop", getApplicationContext()).equals("NOTSTARTED")) {
                    SPVariables.setString("RecordStartOrStop", "STARTED", getApplicationContext());

                    //new countdown(getApplicationContext()).execute();

                    mFloatingView.findViewById(R.id.collapse_view_stop).setVisibility(View.VISIBLE);
                    mFloatingView.findViewById(R.id.collapse_view).setVisibility(View.GONE);
                    mFloatingView.findViewById(R.id.close_btn).setVisibility(View.GONE);
                    backgroundActivity.finish();
                    expandedView.setVisibility(View.GONE);
                    isExpand = 0;

                    ShowNotification("Start");
                    String bubbleShow = SPVariables.getString("ShowBubble", getApplicationContext());
                    if(bubbleShow.equals("FALSE")){
                        mFloatingView.findViewById(R.id.collapse_view_stop).setVisibility(View.GONE);
                        mFloatingView.findViewById(R.id.collapse_view).setVisibility(View.GONE);
                        mFloatingView.findViewById(R.id.close_btn).setVisibility(View.GONE);

                    }

                    Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                            .setContentTitle("Recording")
                            .build();

                    //startForeground(NOTIFICATION_ID, notification);
                    startForeground(NOTIFICATION_ID_2, buildForegroundNotification());

                    Intent intent = new Intent(getApplicationContext(), BlankActivity.class);
                    intent.putExtra("Record", "START");
                    intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    //blankActivity.startRecording();

                } else if (SPVariables.getString("RecordStartOrStop", getApplicationContext()).equals("STARTED")) {
                    SPVariables.setString("RecordStartOrStop", "NOTSTARTED", getApplicationContext());
                }


            }
        });



        mFloatingViewExpand.findViewById(R.id.iv_Record_Cam).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(FloatingViewService.this, "Recording with Camera Start", Toast.LENGTH_SHORT).show();
                //Toast.makeText(FloatingViewService.this, SPVariables.getString("RecordStartOrStop", getApplicationContext()), Toast.LENGTH_SHORT).show();

//                params.x = 600;
//                params.y = 0;
                mWindowManager.updateViewLayout(mFloatingViewExpand, params);
                mWindowManager.updateViewLayout(mFloatingView, params);
                Rparams = params;

                if (SPVariables.getString("RecordStartOrStop", getApplicationContext()).equals("NOTSTARTED")) {
                    SPVariables.setString("RecordStartOrStop", "STARTED", getApplicationContext());

                    //new countdown(getApplicationContext()).execute();

                    mFloatingView.findViewById(R.id.collapse_view_stop).setVisibility(View.VISIBLE);
                    mFloatingView.findViewById(R.id.collapse_view).setVisibility(View.GONE);
                    mFloatingView.findViewById(R.id.close_btn).setVisibility(View.GONE);
                    backgroundActivity.finish();
                    expandedView.setVisibility(View.GONE);
                    isExpand = 0;

                    ShowNotification("Start");
                    String bubbleShow = SPVariables.getString("ShowBubble", getApplicationContext());
                    if(bubbleShow.equals("FALSE")){
                        mFloatingView.findViewById(R.id.collapse_view_stop).setVisibility(View.GONE);
                        mFloatingView.findViewById(R.id.collapse_view).setVisibility(View.GONE);
                        mFloatingView.findViewById(R.id.close_btn).setVisibility(View.GONE);

                    }

                    Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                            .setContentTitle("Recording")
                            .build();
                    //startForeground(NOTIFICATION_ID, notification);
                    startForeground(NOTIFICATION_ID_2, buildForegroundNotification());

                    Intent intent = new Intent(getApplicationContext(), BlankActivity.class);
                    intent.putExtra("Record", "START");
                    intent.putExtra("RecordWithCamera", "YES");
                    intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else if (SPVariables.getString("RecordStartOrStop", getApplicationContext()).equals("STARTED")) {
                    SPVariables.setString("RecordStartOrStop", "NOTSTARTED", getApplicationContext());
                }
            }
        });

        mFloatingViewExpand.findViewById(R.id.iv_Setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FloatingViewService.this, SettingActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_ANIMATION));
                backgroundActivity.finish();
                expandedView.setVisibility(View.GONE);
            }
        });

        mFloatingViewExpand.findViewById(R.id.iv_Home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FloatingViewService.this, MainActivity.class).putExtra("OpenHome", true).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_ANIMATION));
                backgroundActivity.finish();
                expandedView.setVisibility(View.GONE);
            }
        });

        mFloatingViewExpand.findViewById(R.id.iv_Video_Edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FloatingViewService.this, "Coming Soon", Toast.LENGTH_SHORT).show();
                backgroundActivity.finish();
                expandedView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle("Recording")
                .build();

        //startForeground(NOTIFICATION_ID, notification);
        startForeground(NOTIFICATION_ID_2, notification);
        
        return START_NOT_STICKY;
    }

    public int dpToPx(View v, int dp) {
        DisplayMetrics displayMetrics = v.getContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public int pxToDp(View v, int px) {
        DisplayMetrics displayMetrics = v.getContext().getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    /**
     * Detect if the floating view is collapsed or expanded.
     *
     * @return true if the floating view is collapsed.
     */
    private boolean isViewCollapsed() {
        return mFloatingView == null || mFloatingView.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) {
            mWindowManager.removeView(mFloatingView);
            mWindowManager.removeView(mFloatingViewExpand);
        }
        closeNotification();
    }

    private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            //Toast.makeText(FloatingViewService.this, "Long Tap", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void ShowNotification(String Recording) {

        //CreateNotificationChannel();

//        Intent pauseIntent = new Intent(this, BlankActivity.class);
//        pauseIntent.putExtra("pauseRecording", true);
//        //pauseIntent .setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
//        pauseIntent .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pausePIntent = PendingIntent.getActivity(this,0,pauseIntent ,PendingIntent.FLAG_ONE_SHOT);
//
//        Intent PlayIntent = new Intent(this, BlankActivity.class);
//        PlayIntent.putExtra("playRecording", true);
//        PlayIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        //PlayIntent.setFlags(Intent. FLAG_ACTIVITY_CLEAR_TOP | Intent. FLAG_ACTIVITY_SINGLE_TOP);
//        PendingIntent PlayPIntent = PendingIntent.getActivity(this,0,PlayIntent,PendingIntent.FLAG_ONE_SHOT);

//        Intent PauseIntent = new Intent(this,BlankActivity.class);
//        PauseIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent PlayPIntent = PendingIntent.getActivity(this,0,PauseIntent,PendingIntent.FLAG_ONE_SHOT);
//
//        Intent StopIntent = new Intent(this,MainActivity.class);
//        StopIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pausePIntent = PendingIntent.getActivity(this,0,StopIntent,PendingIntent.FLAG_ONE_SHOT);

        String aa = Recording;

        Random generator = new Random();

        Intent StopIntent = new Intent(this,NotiRecordingStop.class);//BlankActivity.class);
        //StopIntent.putExtra("Notification", "stop");
        StopIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pausePIntent = PendingIntent.getActivity(this,generator.nextInt(),StopIntent,PendingIntent.FLAG_ONE_SHOT);


        Intent StopIntent2 = new Intent(this,NotiRecordingStart.class);         //NotiRecordingStart.class
        //StopIntent2.putExtra("OpenHome", "true");
        StopIntent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pausePIntent2 = PendingIntent.getActivity(this, generator.nextInt(),StopIntent2,PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);

        builder.setSmallIcon(R.drawable.logo);
        builder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                R.drawable.logo));
        builder.setContentTitle("Screen Recorder App");
        if(!Recording.isEmpty() && Recording.equals("Start")){
            builder.setContentText("Click here to stop");
        }
        if(!Recording.isEmpty() && Recording.equals("Stop")){
            builder.setContentText("");
        }
        //builder.setContentText("Description of Notification");
        //builder.setPriority(NotificationCompat.PRIORITY_HIGH);

        builder.setChannelId(CHANNEL_ID);
        builder.setAutoCancel(false);
        builder.setOngoing(true);

        String recordingStarted = SPVariables.getString("RecordStartOrStop", getApplicationContext());

        if(recordingStarted != null && recordingStarted.equals("STARTED")){
            //if(!Recording.isEmpty() && Recording.equals("Start")){
            builder.setContentIntent(pausePIntent);
        }
        else {
            builder.setContentIntent(pausePIntent2);
        }

        //if(Recording.isEmpty()){
        /*if(!Recording.isEmpty() && Recording.equals("Stop")){
            //builder.setContentIntent(pausePIntent2);
        }*/
        /*if(recordingStarted != null && recordingStarted.equals("NOTSTARTED")){
            builder.setContentIntent(pausePIntent2);
        }*/
        //builder.addAction(R.drawable.ic_play,"Play",PlayPIntent);
        //builder.addAction(R.drawable.ic_pause,"Pause",pausePIntent);
        //builder.addAction(R.drawable.ic_stop,"Stop",StopPIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());

    }

    public void closeNotification(){
        NotificationManager notificationManager = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);

        notificationManager.cancel(NOTIFICATION_ID);
    }


    private void CreateNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) ;
        {
            CharSequence name = "My Notification";
            String descprtion = "My Notification Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;


            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);

                notificationChannel.setDescription(descprtion);
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    private Notification buildForegroundNotification() {
        NotificationCompat.Builder b=new NotificationCompat.Builder(this, CHANNEL_ID);
        b.setChannelId(CHANNEL_ID);
        b.setOngoing(true)
                .setContentTitle("Screen Recorder");
        return(b.build());
    }
}