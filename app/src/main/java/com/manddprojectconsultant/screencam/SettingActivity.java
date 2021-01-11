package com.manddprojectconsultant.screencam;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
public class SettingActivity extends AppCompatActivity implements CameraPreviewFragment.ItemClickListenercamerapreview,CameraFacingFragment.ItemClickListenercamerafacing ,OrientationFragment.ItemClickListenerorientation, FPSFragment.ItemClickListenerfps, QualitySheetFragment.ItemClickListenervideo, ResolutionSheetDialog.ItemClickListener {
    public static final int REQUEST_CODE_INTRO = 1;
    public static final String PREF_KEY_FIRST_START = "com.heinrichreimersoftware.materialintro.demo.PREF_KEY_FIRST_START";
    private RelativeLayout llResolution, llQuality, llFPS, llOrientation, llRecordAudio, llFileLocation, llCountDown,
            llCamera, llCameraPreview, llShowBubble,llRating,llShare;
    private TextView tvResolution, tvQuality, tvFPS, tvOrientation, tvCamera, tvCameraPreview;
    private Switch swRecordAudio, swCountDown, swAppIntro, swShowBubble;
    ImageView llAppIntro, ivbackbutton;
    //final String[] list = getActivity().getResources().getStringArray(R.array.Choice_items);
    String[] ResolutionList, QualityList, FPSList, OrientationList, CameraList, CameraListPreview;
    public static final String Resolution = "Resolution";
    public static final String VideoQuality = "Quality";
    public static final String FPS = "FPS";
    public static final String Orientation = "Orientation";
    public static final String CameraFacing= "CameraFacing";
    public static final String CameraFrame= "CameraFrame";
    public static final String CameraPreview = "CameraPreview";


   // public static final String RecordAudio = "record audio";




    QualitySheetFragment qualitySheetFragment;
    ResolutionSheetDialog resolutionSheetDialog;
    FPSFragment fpsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        findAllView();


        //Resolution Preference
        String resolution = SPVariables.getString(Resolution, SettingActivity.this);
        tvResolution.setText(resolution);

        //Video Quality Preference
        String videoquality = SPVariables.getString(VideoQuality, SettingActivity.this);
        tvQuality.setText(videoquality);

        //FPS preference
        String fpspre = SPVariables.getString(FPS, SettingActivity.this);
        tvFPS.setText(fpspre);

        //Orientation preference
        String orientationpre = SPVariables.getString(Orientation, SettingActivity.this);
        tvOrientation.setText(orientationpre);

        //CameraFacing preference
        String camerafacingpre = SPVariables.getString(CameraFacing, SettingActivity.this);
        tvCamera.setText(camerafacingpre);


        //CameraPreview Preference
        String camerapreviewpre = SPVariables.getString(CameraPreview, SettingActivity.this);
        tvCameraPreview.setText(camerapreviewpre);





        setTitle("Settings");
        getAllList();
        loadSettingDataFromSP();
        allOnClicks();
    }

    private void loadSettingDataFromSP() {
        //Resolution
        String aa = SPVariables.getString(Resolution,SettingActivity.this);
        if (SPVariables.getString("Resolution", SettingActivity.this).isEmpty()) {
            tvResolution.setText(aa);
        } else {
            //tvResolution.setText(SPVariables.getString("resolution", SettingActivity.this));
            tvResolution.setText(SPVariables.getString("Resolution", SettingActivity.this));
        }

        //Video Quality

        String videoquality = SPVariables.getString(VideoQuality, SettingActivity.this);
        if (SPVariables.getString("Quality", SettingActivity.this).isEmpty()) {
            tvQuality.setText(videoquality);
        } else {
            tvQuality.setText(SPVariables.getString("Quality", SettingActivity.this));
        }

        //FPS
        String fpspre = SPVariables.getString(FPS, SettingActivity.this);
        if (SPVariables.getString("FPS", SettingActivity.this).isEmpty()) {
            tvFPS.setText(fpspre);
        } else {
            tvFPS.setText(SPVariables.getString("FPS", SettingActivity.this));
        }

        //Orientation
        String orientationpre = SPVariables.getString(Orientation, SettingActivity.this);
        if (SPVariables.getString("Orientation", SettingActivity.this).isEmpty()) {
            tvOrientation.setText(orientationpre);
        } else {
            tvOrientation.setText(SPVariables.getString("Orientation", SettingActivity.this));
        }

        //Camera Facing
        String camerafacingpre = SPVariables.getString(CameraFacing, SettingActivity.this);
        if (SPVariables.getString("CameraFacing", SettingActivity.this).isEmpty()) {
            tvCamera.setText(camerafacingpre);
        } else {
            tvCamera.setText(SPVariables.getString("CameraFacing", SettingActivity.this));
        }

        //Camera Preview
        String camerapreviewpre = SPVariables.getString(CameraPreview, SettingActivity.this);
        if (SPVariables.getString("CameraPreview", SettingActivity.this).isEmpty()) {
            tvCameraPreview.setText(camerapreviewpre);
        } else {
            tvCameraPreview.setText(SPVariables.getString("CameraPreview", SettingActivity.this));
        }

        if (SPVariables.getString("RecordAudio", SettingActivity.this).equals("TRUE")) {
            swRecordAudio.setChecked(true);
        } else {
            swRecordAudio.setChecked(false);
        }

        if (SPVariables.getString("CountDown", SettingActivity.this).equals("TRUE")) {
            swCountDown.setChecked(true);
        } else {
            swCountDown.setChecked(false);
        }
//        if (SPVariables.getString("AppIntro", SettingActivity.this).equals("TRUE")) {
//            swAppIntro.setChecked(true);
//        } else {
//            swAppIntro.setChecked(false);
//        }
        if (SPVariables.getString("ShowBubble", SettingActivity.this).equals("TRUE")) {
            swShowBubble.setChecked(true);
        } else {
            swShowBubble.setChecked(false);
        }
    }

    private void allOnClicks() {
        llResolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*DialogFragment SingleChoiceDialog = new SingleChoiceDialogFragment(ResolutionList, "Resolution", tvResolution.getText().toString());
                SingleChoiceDialog.setCancelable(true);
                SingleChoiceDialog.show(getSupportFragmentManager(), "Single Choice Dialog");*/
                resolutionSheetDialog = new ResolutionSheetDialog();
                resolutionSheetDialog.show(getSupportFragmentManager(), "Resolution");
            }
        });
        llQuality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           /*     DialogFragment SingleChoiceDialog = new SingleChoiceDialogFragment(QualityList, "Quality", tvQuality.getText().toString());
                SingleChoiceDialog.setCancelable(true);
                SingleChoiceDialog.show(getSupportFragmentManager(), "Single Choice Dialog");*/
                qualitySheetFragment = new QualitySheetFragment();
                qualitySheetFragment.show(getSupportFragmentManager(), "Quality");
            }
        });
        llFPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*DialogFragment SingleChoiceDialog = new SingleChoiceDialogFragment(FPSList, "FPS", tvFPS.getText().toString());
                SingleChoiceDialog.setCancelable(true);
                SingleChoiceDialog.show(getSupportFragmentManager(), "Single Choice Dialog");*/
                fpsFragment = new FPSFragment();
                fpsFragment.show(getSupportFragmentManager(), "FPS");
            }
        });
        llOrientation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* DialogFragment SingleChoiceDialog = new SingleChoiceDialogFragment(OrientationList, "Orientation", tvOrientation.getText().toString());
                SingleChoiceDialog.setCancelable(true);
                SingleChoiceDialog.show(getSupportFragmentManager(), "Single Choice Dialog");*/

                OrientationFragment orientationFragment=new OrientationFragment();
                orientationFragment.show(getSupportFragmentManager(),"Orientation");


            }
        });
        llCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        /*        DialogFragment SingleChoiceDialog = new SingleChoiceDialogFragment(CameraList, "Camera", tvCamera.getText().toString());
                SingleChoiceDialog.setCancelable(true);
                SingleChoiceDialog.show(getSupportFragmentManager(), "Single Choice Dialog");*/

                CameraFacingFragment cameraFacingFragment=new CameraFacingFragment();
                cameraFacingFragment.show(getSupportFragmentManager(),"CameraFacing");


            }
        });
        llCameraPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        /*        DialogFragment SingleChoiceDialog = new SingleChoiceDialogFragment(CameraListPreview, "CameraPreview", tvCameraPreview.getText().toString());
                SingleChoiceDialog.setCancelable(true);
                SingleChoiceDialog.show(getSupportFragmentManager(), "Single Choice Dialog");*/

                CameraPreviewFragment cameraPreviewFragment=new CameraPreviewFragment();
                cameraPreviewFragment.show(getSupportFragmentManager(),"Camera Preview");

            }
        });






        swRecordAudio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SPVariables.setString("RecordAudio", "TRUE", SettingActivity.this);
                } else {
                    SPVariables.setString("RecordAudio", "FALSE", SettingActivity.this);
                }
            }
        });
        swCountDown.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SPVariables.setString("CountDown", "TRUE", SettingActivity.this);
                } else {
                    SPVariables.setString("CountDown", "FALSE", SettingActivity.this);
                }
            }
        });
        llAppIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefManager prefManager = new PrefManager(SettingActivity.this);
                prefManager.setFirstTimeLaunch(true);
                Intent intent = new Intent(SettingActivity.this, Welcome_Screen.class);
                startActivityForResult(intent, REQUEST_CODE_INTRO);
                //finish();
            }
        });
//        swAppIntro.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    SPVariables.setString("AppIntro", "TRUE", SettingActivity.this);
//                } else {
//                    SPVariables.setString("AppIntro", "FALSE", SettingActivity.this);
//                }
//            }
//        });
        swShowBubble.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SPVariables.setString("ShowBubble", "TRUE", SettingActivity.this);
                } else {
                    SPVariables.setString("ShowBubble", "FALSE", SettingActivity.this);
                }
            }
        });
        ivbackbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SettingActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
        super.onBackPressed();
    }

    private void getAllList() {
        ResolutionList = getResources().getStringArray(R.array.Resolution);
        QualityList = getResources().getStringArray(R.array.Quality);
        FPSList = getResources().getStringArray(R.array.FPS);
        OrientationList = getResources().getStringArray(R.array.Orientation);
        CameraList = getResources().getStringArray(R.array.Camera);
        CameraListPreview = getResources().getStringArray(R.array.CameraPreview);
    }

    private void findAllView() {
        ivbackbutton = findViewById(R.id.ivbackbutton);
        llResolution = findViewById(R.id.llResolution);
        llQuality = findViewById(R.id.llQuality);
        llFPS = findViewById(R.id.llFPS);
        llOrientation = findViewById(R.id.llOrientation);
        llRecordAudio = findViewById(R.id.llRecordAudio);
        llFileLocation = findViewById(R.id.llFileLocation);
        llCountDown = findViewById(R.id.llCountDown);
        llCamera = findViewById(R.id.llCamera);
        llCameraPreview = findViewById(R.id.llCameraPreview);
        llAppIntro = findViewById(R.id.llAppIntro);
        llShowBubble = findViewById(R.id.llShowBubble);
        tvResolution = findViewById(R.id.tvResolution);
        tvQuality = findViewById(R.id.tvQuality);
        tvFPS = findViewById(R.id.tvFPS);
        tvOrientation = findViewById(R.id.tvOrientation);
        tvCamera = findViewById(R.id.tvCamera);
        tvCameraPreview = findViewById(R.id.tvCameraPreview);
        swRecordAudio = findViewById(R.id.swRecordAudio);
        swCountDown = findViewById(R.id.swCountDown);
        //swAppIntro = findViewById(R.id.swAppIntro);
        swShowBubble = findViewById(R.id.swShowBubble);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_INTRO) {
            if (resultCode == RESULT_OK) {
                PreferenceManager.getDefaultSharedPreferences(this).edit()
                        .putBoolean(PREF_KEY_FIRST_START, false)
                        .apply();
            } else {
                PreferenceManager.getDefaultSharedPreferences(this).edit()
                        .putBoolean(PREF_KEY_FIRST_START, true)
                        .apply();
                //User cancelled the intro so we'll finish this activity too.
                //finish();
            }
        }
    }

    @Override
    public void onItemClick(String item) {
        String resolution = item.toString();
        SPVariables.setString(Resolution, resolution, SettingActivity.this);
        startActivity(new Intent(SettingActivity.this, SettingActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
        finish();
        overridePendingTransition(0, 0);
        //tvResolution.setText(resolution);
    }

    @Override
    public void onItemClickvideo(String videoquality) {
        String vidquality = videoquality.toString();
        SPVariables.setString(VideoQuality, vidquality, SettingActivity.this);
        startActivity(new Intent(SettingActivity.this, SettingActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onItemClickfps(String fps) {
        String fp = fps.toString();
        SPVariables.setString(FPS, fp, SettingActivity.this);
        startActivity(new Intent(SettingActivity.this, SettingActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onItemClickorientation(String orientation) {
        String orien = orientation.toString();
        SPVariables.setString(Orientation, orien, SettingActivity.this);
        startActivity(new Intent(SettingActivity.this, SettingActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onItemClickcamerafacing(String camerafacing) {
        String camera = camerafacing.toString();
        SPVariables.setString(CameraFacing, camera, SettingActivity.this);
        startActivity(new Intent(SettingActivity.this, SettingActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
        finish();
        overridePendingTransition(0, 0);


    }

    @Override
    public void onItemClickcamerapreview(String camerapreview) {

        String camerapre = camerapreview.toString();

        SPVariables.setString(CameraPreview, camerapre, SettingActivity.this);

        startActivity(new Intent(SettingActivity.this, SettingActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
        finish();
        overridePendingTransition(0, 0);

    }

    public void Rating(View view) {

        //Rating bar code

        /*Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (Exception e) {
            Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show();
        }*/


        //Please check this out upwards code
        Toast.makeText(this, "Coming Soon...", Toast.LENGTH_SHORT).show();


    }

    public void Sharethelink(View view) {


        //Please check this out upwards code
        Toast.makeText(this, "Coming Soon...", Toast.LENGTH_SHORT).show();


    }
}
