package com.manddprojectconsultant.screencam.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.manddprojectconsultant.screencam.BuildConfig
import com.manddprojectconsultant.screencam.R
import com.manddprojectconsultant.screencam.databinding.ActivitySettingBinding
import com.manddprojectconsultant.screencam.fragment.CameraFacingFragment
import com.manddprojectconsultant.screencam.fragment.CameraFacingFragment.ItemClickListenercamerafacing
import com.manddprojectconsultant.screencam.fragment.CameraPreviewFragment
import com.manddprojectconsultant.screencam.fragment.CameraPreviewFragment.ItemClickListenercamerapreview
import com.manddprojectconsultant.screencam.fragment.FPSFragment
import com.manddprojectconsultant.screencam.fragment.FPSFragment.ItemClickListenerfps
import com.manddprojectconsultant.screencam.fragment.OrientationFragment
import com.manddprojectconsultant.screencam.fragment.OrientationFragment.ItemClickListenerorientation
import com.manddprojectconsultant.screencam.fragment.QualitySheetFragment
import com.manddprojectconsultant.screencam.fragment.QualitySheetFragment.ItemClickListenervideo
import com.manddprojectconsultant.screencam.utils.PreferenceManager
import com.manddprojectconsultant.screencam.utils.SPVariables
import com.manddprojectconsultant.screencam.widget.ResolutionSheetDialog
import com.manddprojectconsultant.screencam.widget.ResolutionSheetDialog.ItemClickListener
import androidx.core.net.toUri

class SettingActivity : AppCompatActivity(), ItemClickListenercamerapreview,
    ItemClickListenercamerafacing, ItemClickListenerorientation, ItemClickListenerfps,
    ItemClickListenervideo, ItemClickListener {
        lateinit var binding: ActivitySettingBinding
//    private var binding.llResolution: RelativeLayout? = null
//    private var binding.llQuality: RelativeLayout? = null
//    private var binding.llFPS: RelativeLayout? = null
//    private var binding.llOrientation: RelativeLayout? = null
//    private var binding.llRecordAudio: RelativeLayout? = null
//    private var binding.llFileLocation: RelativeLayout? = null
//    private var binding.llCountDown: RelativeLayout? = null
//    private var binding.llCamera: RelativeLayout? = null
//    private var binding.llCameraPreview: RelativeLayout? = null
//    private var binding.llShowBubble: RelativeLayout? = null
//    private val llRating: RelativeLayout? = null
//    private val llShare: RelativeLayout? = null
//    private var binding.tvResolution: TextView? = null
//    private var binding.tvQuality: TextView? = null
//    private var binding.tvFPS: TextView? = null
//    private var binding.tvOrientation: TextView? = null
//    private var binding.tvCamera: TextView? = null
//    private var binding.tvCameraPreview: TextView? = null
//    private var binding.swRecordAudio: Switch? = null
//    private var binding.swCountDown: Switch? = null
//    private val swAppIntro: Switch? = null
//    private var binding.swShowBubble: Switch? = null
//    private var binding.llAppIntro: ImageView? = null
//    private var binding.ivbackbutton: ImageView? = null

    //final String[] list = getActivity().getResources().getStringArray(R.array.Choice_items);
    private var resolutionList: Array<String> = arrayOf()
    private var qualityList: Array<String> = arrayOf()
    private var fpsList: Array<String> = arrayOf()
    private var orientationList: Array<String> = arrayOf()
    private var cameraList: Array<String> = arrayOf()
    private var cameraListPreview: Array<String> = arrayOf()

    // public static final String RecordAudio = "record audio";
    private var qualitySheetFragment: QualitySheetFragment = QualitySheetFragment()
    private var resolutionSheetDialog: ResolutionSheetDialog = ResolutionSheetDialog()
    private var fpsFragment: FPSFragment = FPSFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        findAllView()


        //Resolution Preference
        val resolution = SPVariables.getString(Resolution, this@SettingActivity)
        binding.tvResolution.text = resolution

        //Video Quality Preference
        val videoquality = SPVariables.getString(VideoQuality, this@SettingActivity)
        binding.tvQuality.text = videoquality

        //FPS preference
        val fpspre = SPVariables.getString(FPS, this@SettingActivity)
        binding.tvFPS.text = fpspre

        //Orientation preference
        val orientationpre = SPVariables.getString(Orientation, this@SettingActivity)
        binding.tvOrientation.text = orientationpre

        //CameraFacing preference
        val camerafacingpre = SPVariables.getString(cameraFacing, this@SettingActivity)
        binding.tvCamera.text = camerafacingpre


        //CameraPreview Preference
        val camerapreviewpre = SPVariables.getString(cameraPreview, this@SettingActivity)
        binding.tvCameraPreview.text = camerapreviewpre





        title = "Settings"
        allList
        loadSettingDataFromSP()
        allOnClicks()
    }

    private fun loadSettingDataFromSP() {
        //Resolution
        val aa = SPVariables.getString(Resolution, this@SettingActivity)
        if (SPVariables.getString("Resolution", this@SettingActivity).isEmpty()) {
            binding.tvResolution.text = aa
        } else {
            //binding.tvResolution.setText(SPVariables.getString("resolution", SettingActivity.this));
            binding.tvResolution.text =
                SPVariables.getString("Resolution", this@SettingActivity)
        }

        //Video Quality
        val videoquality = SPVariables.getString(VideoQuality, this@SettingActivity)
        if (SPVariables.getString("Quality", this@SettingActivity).isEmpty()) {
            binding.tvQuality.text = videoquality
        } else {
            binding.tvQuality.text = SPVariables.getString("Quality", this@SettingActivity)
        }

        //FPS
        val fpspre = SPVariables.getString(FPS, this@SettingActivity)
        if (SPVariables.getString("FPS", this@SettingActivity).isEmpty()) {
            binding.tvFPS.text = fpspre
        } else {
            binding.tvFPS.text = SPVariables.getString("FPS", this@SettingActivity)
        }

        //Orientation
        val orientationpre = SPVariables.getString(Orientation, this@SettingActivity)
        if (SPVariables.getString("Orientation", this@SettingActivity).isEmpty()) {
            binding.tvOrientation.text = orientationpre
        } else {
            binding.tvOrientation.text =
                SPVariables.getString("Orientation", this@SettingActivity)
        }

        //Camera Facing
        val camerafacingpre = SPVariables.getString(cameraFacing, this@SettingActivity)
        if (SPVariables.getString("CameraFacing", this@SettingActivity).isEmpty()) {
            binding.tvCamera.text = camerafacingpre
        } else {
            binding.tvCamera.text =
                SPVariables.getString("CameraFacing", this@SettingActivity)
        }

        //Camera Preview
        val camerapreviewpre = SPVariables.getString(cameraPreview, this@SettingActivity)
        if (SPVariables.getString("CameraPreview", this@SettingActivity).isEmpty()) {
            binding.tvCameraPreview.text = camerapreviewpre
        } else {
            binding.tvCameraPreview.text =
                SPVariables.getString("CameraPreview", this@SettingActivity)
        }

        if (SPVariables.getString("RecordAudio", this@SettingActivity) == "TRUE") {
            binding.swRecordAudio.isChecked = true
        } else {
            binding.swRecordAudio.isChecked = false
        }

        if (SPVariables.getString("CountDown", this@SettingActivity) == "TRUE") {
            binding.swCountDown.isChecked = true
        } else {
            binding.swCountDown.isChecked = false
        }
        //        if (SPVariables.getString("AppIntro", SettingActivity.this).equals("TRUE")) {
//            swAppIntro.setChecked(true);
//        } else {
//            swAppIntro.setChecked(false);
//        }
        if (SPVariables.getString("ShowBubble", this@SettingActivity) == "TRUE") {
            binding.swShowBubble.isChecked = true
        } else {
            binding.swShowBubble.isChecked = false
        }
    }

    private fun allOnClicks() {
        binding.llResolution.setOnClickListener { /*DialogFragment SingleChoiceDialog = new SingleChoiceDialogFragment(ResolutionList, "Resolution", binding.tvResolution.getText().toString());
                    SingleChoiceDialog.setCancelable(true);
                    SingleChoiceDialog.show(getSupportFragmentManager(), "Single Choice Dialog");*/
            resolutionSheetDialog = ResolutionSheetDialog()
            resolutionSheetDialog.show(supportFragmentManager, "Resolution")
        }
        binding.llQuality.setOnClickListener { /*     DialogFragment SingleChoiceDialog = new SingleChoiceDialogFragment(QualityList, "Quality", binding.tvQuality.getText().toString());
                    SingleChoiceDialog.setCancelable(true);
                    SingleChoiceDialog.show(getSupportFragmentManager(), "Single Choice Dialog");*/
            qualitySheetFragment = QualitySheetFragment()
            qualitySheetFragment.show(supportFragmentManager, "Quality")
        }
        binding.llFPS.setOnClickListener { /*DialogFragment SingleChoiceDialog = new SingleChoiceDialogFragment(FPSList, "FPS", binding.tvFPS.getText().toString());
                    SingleChoiceDialog.setCancelable(true);
                    SingleChoiceDialog.show(getSupportFragmentManager(), "Single Choice Dialog");*/
            fpsFragment = FPSFragment()
            fpsFragment.show(supportFragmentManager, "FPS")
        }
        binding.llOrientation.setOnClickListener { /* DialogFragment SingleChoiceDialog = new SingleChoiceDialogFragment(OrientationList, "Orientation", binding.tvOrientation.getText().toString());
                    SingleChoiceDialog.setCancelable(true);
                    SingleChoiceDialog.show(getSupportFragmentManager(), "Single Choice Dialog");*/
            val orientationFragment = OrientationFragment()
            orientationFragment.show(supportFragmentManager, "Orientation")
        }
        binding.llCamera.setOnClickListener { /*        DialogFragment SingleChoiceDialog = new SingleChoiceDialogFragment(CameraList, "Camera", binding.tvCamera.getText().toString());
                    SingleChoiceDialog.setCancelable(true);
                    SingleChoiceDialog.show(getSupportFragmentManager(), "Single Choice Dialog");*/
            val cameraFacingFragment = CameraFacingFragment()
            cameraFacingFragment.show(supportFragmentManager, "CameraFacing")
        }
        binding.llCameraPreview.setOnClickListener { /*        DialogFragment SingleChoiceDialog = new SingleChoiceDialogFragment(CameraListPreview, "CameraPreview", binding.tvCameraPreview.getText().toString());
                    SingleChoiceDialog.setCancelable(true);
                    SingleChoiceDialog.show(getSupportFragmentManager(), "Single Choice Dialog");*/
            val cameraPreviewFragment = CameraPreviewFragment()
            cameraPreviewFragment.show(supportFragmentManager, "Camera Preview")
        }






        binding.swRecordAudio.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                SPVariables.setString("RecordAudio", "TRUE", this@SettingActivity)
            } else {
                SPVariables.setString("RecordAudio", "FALSE", this@SettingActivity)
            }
        }
        binding.swCountDown.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                SPVariables.setString("CountDown", "TRUE", this@SettingActivity)
            } else {
                SPVariables.setString("CountDown", "FALSE", this@SettingActivity)
            }
        }
        binding.llAppIntro.setOnClickListener {
            val preferenceManager =
                PreferenceManager(this@SettingActivity)
            preferenceManager.isFirstTimeLaunch = true
            val intent = Intent(this@SettingActivity, WelcomeActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_INTRO)
            //finish();
        }
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
        binding.swShowBubble.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                SPVariables.setString("ShowBubble", "TRUE", this@SettingActivity)
            } else {
                SPVariables.setString("ShowBubble", "FALSE", this@SettingActivity)
            }
        }
        binding.ivbackbutton.setOnClickListener { onBackPressed() }
    }

    override fun onBackPressed() {
        val intent = Intent(this@SettingActivity, DashboardActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
        super.onBackPressed()
    }

    private val allList: Unit
        get() {
            resolutionList = resources.getStringArray(R.array.Resolution)
            qualityList = resources.getStringArray(R.array.Quality)
            fpsList = resources.getStringArray(R.array.FPS)
            orientationList = resources.getStringArray(R.array.Orientation)
            cameraList = resources.getStringArray(R.array.Camera)
            cameraListPreview = resources.getStringArray(R.array.CameraPreview)
        }

    private fun findAllView() {
//        binding.ivbackbutton = findViewById(R.id.binding.ivbackbutton)
//        binding.llResolution = findViewById(R.id.binding.llResolution)
//        binding.llQuality = findViewById(R.id.binding.llQuality)
//        binding.llFPS = findViewById(R.id.binding.llFPS)
//        binding.llOrientation = findViewById(R.id.binding.llOrientation)
//        binding.llRecordAudio = findViewById(R.id.binding.llRecordAudio)
//        binding.llFileLocation = findViewById(R.id.binding.llFileLocation)
//        binding.llCountDown = findViewById(R.id.binding.llCountDown)
//        binding.llCamera = findViewById(R.id.binding.llCamera)
//        binding.llCameraPreview = findViewById(R.id.binding.llCameraPreview)
//        binding.llAppIntro = findViewById(R.id.binding.llAppIntro)
//        binding.llShowBubble = findViewById(R.id.binding.llShowBubble)
//        binding.tvResolution = findViewById(R.id.binding.tvResolution)
//        binding.tvQuality = findViewById(R.id.binding.tvQuality)
//        binding.tvFPS = findViewById(R.id.binding.tvFPS)
//        binding.tvOrientation = findViewById(R.id.binding.tvOrientation)
//        binding.tvCamera = findViewById(R.id.binding.tvCamera)
//        binding.tvCameraPreview = findViewById(R.id.binding.tvCameraPreview)
//        binding.swRecordAudio = findViewById(R.id.binding.swRecordAudio)
//        binding.swCountDown = findViewById(R.id.binding.swCountDown)
//        //swAppIntro = findViewById(R.id.swAppIntro);
//        binding.swShowBubble = findViewById(R.id.binding.swShowBubble)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_INTRO) {
            if (resultCode == RESULT_OK) {
                android.preference.PreferenceManager.getDefaultSharedPreferences(this).edit()
                    .putBoolean(PREF_KEY_FIRST_START, false)
                    .apply()
            } else {
                android.preference.PreferenceManager.getDefaultSharedPreferences(this).edit()
                    .putBoolean(PREF_KEY_FIRST_START, true)
                    .apply()
                //User cancelled the intro so we'll finish this activity too.
                //finish();
            }
        }
    }

    override fun onItemClick(item: String) {
        val resolution = item
        SPVariables.setString(Resolution, resolution, this@SettingActivity)
        startActivity(
            Intent(
                this@SettingActivity,
                SettingActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        )
        finish()
        overridePendingTransition(0, 0)
        //binding.tvResolution.setText(resolution);
    }

    override fun onItemClickvideo(videoquality: String) {
        val vidquality = videoquality
        SPVariables.setString(VideoQuality, vidquality, this@SettingActivity)
        startActivity(
            Intent(
                this@SettingActivity,
                SettingActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        )
        finish()
        overridePendingTransition(0, 0)
    }

    override fun onItemClickfps(fps: String) {
        val fp = fps
        SPVariables.setString(FPS, fp, this@SettingActivity)
        startActivity(
            Intent(
                this@SettingActivity,
                SettingActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        )
        finish()
        overridePendingTransition(0, 0)
    }

    override fun onItemClickorientation(orientation: String) {
        val orien = orientation
        SPVariables.setString(Orientation, orien, this@SettingActivity)
        startActivity(
            Intent(
                this@SettingActivity,
                SettingActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        )
        finish()
        overridePendingTransition(0, 0)
    }

    override fun onItemClickcamerafacing(camerafacing: String) {
        val camera = camerafacing
        SPVariables.setString(cameraFacing, camera, this@SettingActivity)
        startActivity(
            Intent(
                this@SettingActivity,
                SettingActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        )
        finish()
        overridePendingTransition(0, 0)
    }

    override fun onItemClickcamerapreview(camerapreview: String) {
        val camerapre = camerapreview

        SPVariables.setString(cameraPreview, camerapre, this@SettingActivity)

        startActivity(
            Intent(
                this@SettingActivity,
                SettingActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        )
        finish()
        overridePendingTransition(0, 0)
    }

    fun Rating() {
        //Rating bar code

        val uri = "market://details?id=$packageName".toUri()
        val myAppLinkToMarket = Intent(Intent.ACTION_VIEW, uri)
        try {
            startActivity(myAppLinkToMarket)
        } catch (e: Exception) {
            Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show()
        }


        /*//Please check this out upwards code
        Toast.makeText(this, "Coming Soon...", Toast.LENGTH_SHORT).show();
*/
    }

    fun shareTheLink() {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("text/plain")
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Screen Cam")
            var shareMessage = "\nPlease recommend this app to your circle.\n"
            shareMessage =
                shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n"
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, "choose one"))
        } catch (e: Exception) {
            //e.toString();
        }
    }

    companion object {
        const val REQUEST_CODE_INTRO: Int = 1
        const val PREF_KEY_FIRST_START: String = "PREF_KEY_FIRST_START"
        const val Resolution: String = "Resolution"
        const val VideoQuality: String = "Quality"
        const val FPS: String = "FPS"
        const val Orientation: String = "Orientation"
        const val cameraFacing: String = "CameraFacing"
        const val cameraFrame: String = "CameraFrame"
        const val cameraPreview: String = "CameraPreview"
    }
}
