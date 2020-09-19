package com.example.screenrecorder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class SettingActivity extends AppCompatActivity implements SingleChoiceDialogFragment.SingleChoiceListner {

    public static final int REQUEST_CODE_INTRO = 1;
    public static final String PREF_KEY_FIRST_START = "com.heinrichreimersoftware.materialintro.demo.PREF_KEY_FIRST_START";

    private LinearLayout llResolution, llQuality, llFPS, llOrientation, llRecordAudio, llFileLocation, llCountDown,
            llCamera, llCameraPreview, llAppIntro, llShowBubble;

    private TextView tvResolution, tvQuality, tvFPS, tvOrientation, tvCamera, tvCameraPreview;
    private Switch swRecordAudio, swCountDown, swAppIntro, swShowBubble;

    //final String[] list = getActivity().getResources().getStringArray(R.array.Choice_items);
    String[] ResolutionList, QualityList, FPSList, OrientationList, CameraList, CameraListPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        setTitle("Settings");
        findAllView();
        getAllList();
        loadSettingDataFromSP();
        allOnClicks();
    }

    private void loadSettingDataFromSP() {
        if (SPVariables.getString("Resolution", SettingActivity.this).isEmpty()) {
            tvResolution.setText("720P");
        } else {
            tvResolution.setText(SPVariables.getString("Resolution", SettingActivity.this));
        }

        if (SPVariables.getString("Quality", SettingActivity.this).isEmpty()) {
            tvQuality.setText("Auto");
        } else {
            tvQuality.setText(SPVariables.getString("Quality", SettingActivity.this));
        }

        if (SPVariables.getString("FPS", SettingActivity.this).isEmpty()) {
            tvFPS.setText("Auto");
        } else {
            tvFPS.setText(SPVariables.getString("FPS", SettingActivity.this));
        }

        if (SPVariables.getString("Orientation", SettingActivity.this).isEmpty()) {
            tvOrientation.setText("Auto");
        } else {
            tvOrientation.setText(SPVariables.getString("Orientation", SettingActivity.this));
        }

        if (SPVariables.getString("Camera", SettingActivity.this).isEmpty()) {
            tvCamera.setText("Auto");
        } else {
            tvCamera.setText(SPVariables.getString("Camera", SettingActivity.this));
        }

        if (SPVariables.getString("CameraPreview", SettingActivity.this).isEmpty()) {
            tvCameraPreview.setText("Medium");
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
                DialogFragment SingleChoiceDialog = new SingleChoiceDialogFragment(ResolutionList, "Resolution", tvResolution.getText().toString());
                SingleChoiceDialog.setCancelable(true);
                SingleChoiceDialog.show(getSupportFragmentManager(), "Single Choice Dialog");
            }
        });

        llQuality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment SingleChoiceDialog = new SingleChoiceDialogFragment(QualityList, "Quality", tvQuality.getText().toString());
                SingleChoiceDialog.setCancelable(true);
                SingleChoiceDialog.show(getSupportFragmentManager(), "Single Choice Dialog");
            }
        });

        llFPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment SingleChoiceDialog = new SingleChoiceDialogFragment(FPSList, "FPS", tvFPS.getText().toString());
                SingleChoiceDialog.setCancelable(true);
                SingleChoiceDialog.show(getSupportFragmentManager(), "Single Choice Dialog");
            }
        });

        llOrientation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment SingleChoiceDialog = new SingleChoiceDialogFragment(OrientationList, "Orientation", tvOrientation.getText().toString());
                SingleChoiceDialog.setCancelable(true);
                SingleChoiceDialog.show(getSupportFragmentManager(), "Single Choice Dialog");
            }
        });

        llCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment SingleChoiceDialog = new SingleChoiceDialogFragment(CameraList, "Camera", tvCamera.getText().toString());
                SingleChoiceDialog.setCancelable(true);
                SingleChoiceDialog.show(getSupportFragmentManager(), "Single Choice Dialog");
            }
        });

        llCameraPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment SingleChoiceDialog = new SingleChoiceDialogFragment(CameraListPreview, "CameraPreview", tvCameraPreview.getText().toString());
                SingleChoiceDialog.setCancelable(true);
                SingleChoiceDialog.show(getSupportFragmentManager(), "Single Choice Dialog");
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
                Intent intent = new Intent(SettingActivity.this, IntroActivity.class);
                startActivityForResult(intent, REQUEST_CODE_INTRO);
                finish();
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
    public void onPositiveButtonClicked(String[] list, int position, String settingName) {

        if (settingName.equals("Resolution")) {
            tvResolution.setText(ResolutionList[position]);
            SPVariables.setString("Resolution", ResolutionList[position], SettingActivity.this);
            Log.e("Resolution: ", SPVariables.getString("Resolution", SettingActivity.this));
        }
        if (settingName.equals("Quality")) {
            tvQuality.setText(QualityList[position]);
            SPVariables.setString("Quality", QualityList[position], SettingActivity.this);
        }
        if (settingName.equals("FPS")) {
            tvFPS.setText(FPSList[position]);
            SPVariables.setString("FPS", FPSList[position], SettingActivity.this);
        }
        if (settingName.equals("Orientation")) {
            tvOrientation.setText(OrientationList[position]);
            SPVariables.setString("Orientation", OrientationList[position], SettingActivity.this);
        }
        if (settingName.equals("Camera")) {
            tvCamera.setText(CameraList[position]);
            SPVariables.setString("Camera", CameraList[position], SettingActivity.this);
        }

        if (settingName.equals("CameraPreview")) {
            tvCameraPreview.setText(CameraListPreview[position]);
            SPVariables.setString("CameraPreview", CameraListPreview[position], SettingActivity.this);
        }
    }

    @Override
    public void onNagativeButtonClicked() {

    }
}
