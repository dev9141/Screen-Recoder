package com.manddprojectconsultant.screencam.activity

import android.Manifest
import android.Manifest.permission
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.manddprojectconsultant.screencam.R
import com.manddprojectconsultant.screencam.adapter.VideoListAdapter
import com.manddprojectconsultant.screencam.databinding.ActivityDashboardBinding
import com.manddprojectconsultant.screencam.fragment.HorizontalListFragment
import com.manddprojectconsultant.screencam.model.VideoModel
import com.manddprojectconsultant.screencam.service.FloatingViewService
import com.manddprojectconsultant.screencam.utils.SPVariables
import com.manddprojectconsultant.screencam.viewModels.DashboardViewModel
import com.manddprojectconsultant.screencam.utils.PermissionManager
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

class DashboardActivity : AppCompatActivity() {
    var binding: ActivityDashboardBinding? = null
    var width = 0
    @JvmField
    var height = 0
    @JvmField
    var heightU = 0
    @JvmField
    var heightB = 0
    var SHOWCASE_ID = "custom example"
    var adaper: VideoListAdapter? = null
    var lstVideo: ArrayList<VideoModel> = arrayListOf()
    var firstStart = false
    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var permissionManager: PermissionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        
        permissionManager = PermissionManager(this)
        
        // Check permissions when activity starts
        if (!permissionManager.checkAndRequestPermissions()) {
            // Permissions not granted, wait for onRequestPermissionsResult
            return
        }
        
        // Continue with normal initialization
        initializeApp()
    }

    private fun initializeApp() {
        dashboardViewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
        Adshow()
        onClickForGridView()
        
        firstStart = PreferenceManager.getDefaultSharedPreferences(this)
            .getBoolean(PREF_KEY_FIRST_START, true)
        FloatingViewService.dashboardActivity = this
        
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        width = size.x
        height = size.y
        Log.e("Widget Height:", " $height")
        //1440 x 2621       1440 x 2541     720 x 1184
        heightU = 20 * height / 100
        heightB = 90 * height / 100
        lstVideo = ArrayList()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName"))
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION)
        }
        
        //setAllSP if SP is empty
        setSP(this@DashboardActivity)
        checkOrCreateFolder()
        initializeView()
        loadVideos()
    }

    fun loadVideos(){
        dashboardViewModel.loadVideos(this){
            if(it.isNotEmpty()){
                lstVideo.apply {
                    clear()
                    addAll(it)
                }
                adaper?.notifyDataSetChanged()
            }
        }
    }

    fun adapterSetup(){
        adaper = VideoListAdapter(lstVideo, this)
        binding!!.rvVideoList1.adapter = adaper
        binding!!.rvVideoList1.layoutManager = LinearLayoutManager(this)
    }

    private fun Adshow() {
        MobileAds.initialize(this) { initializationStatus ->
            // Initialization complete
            val adRequest = AdRequest.Builder().build()
            binding!!.adsinlistview.loadAd(adRequest)
        }
    }

    private fun onClickForGridView() {
        binding!!.ivlistforgridview.setOnClickListener { //loadFragment(new FirstFragment());
            val gridlayout = Intent(this@DashboardActivity, GridLayoutActivity::class.java)
            gridlayout.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            gridlayout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(gridlayout)
            overridePendingTransition(0, 0)
            finish()
        }
    }

    private fun presentShowcaseSequence() {
        val config = ShowcaseConfig()
        config.delay = 500 // half second between each showcase view
        val sequence = MaterialShowcaseSequence(this, SHOWCASE_ID)
        sequence.setOnItemShownListener { itemView, position ->
            //Toast.makeText(itemView.getContext(), "Item #" + position, Toast.LENGTH_SHORT).show();
        }
        sequence.setConfig(config)
        sequence.addSequenceItem(
                MaterialShowcaseView.Builder(this)
                        .setTarget(binding!!.ivlistforgridview)
                        .setDismissText("GOT IT")
                        .setContentText("Click here for list /grid view")
                        .setMaskColour(resources.getColor(R.color.coloryellow))
                        .build()
        )
        sequence.addSequenceItem(
                MaterialShowcaseView.Builder(this)
                        .setTarget(binding!!.ivsetting)
                        .setDismissText("GOT IT")
                        .setContentText("Set Video Configuration of resolution, frame, video quality, audio, camera view")
                        .setMaskColour(resources.getColor(R.color.coloryellow))
                        .build()
        )
        sequence.start()
    }

    private fun loadFragment() {
        supportFragmentManager.beginTransaction().replace(binding!!.dashboardFrameLayout.id, HorizontalListFragment()).commit()
    }

    protected fun checkOrCreateFolder() {
        try {
            val folderName = resources.getString(R.string.main_folder_name)
            val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).toString() + File.separator + folderName)
            if (!file.exists()) {
                file.mkdirs()
            }
            val recordFolder = File(file, "Recording")
            if (!recordFolder.exists()) {
                recordFolder.mkdirs()
            }
            val tempFolder = File(file, ".temp")
            if (!tempFolder.exists()) {
                tempFolder.mkdirs()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        //loadRecordedVideos();
        if (CheckingPermissionIsEnabledOrNot()) {
            loadVideos()
        }
    }

    private fun initializeView() {
        adapterSetup()
        presentShowcaseSequence()
        startFloatingService()
    }

    private fun startFloatingService() {
        // Start the service
        startService(Intent(this@DashboardActivity, FloatingViewService::class.java))
    }

    fun SettingClick(view: View) {
        if (view === binding!!.ivsetting) {
            presentShowCaseForSettings(0)
        }
        startActivity(Intent(this, SettingActivity::class.java))
    }

    private fun presentShowCaseForSettings(i: Int) {
        MaterialShowcaseView.Builder(this)
                .setTarget(binding!!.ivsetting)
                .setGravity(32)
                .setContentText("Settings, When you click on this button it show Settings.")
                .setDismissText("GOT IT")
                .setShapePadding(30)
                .setDelay(3000)
                .setTooltipMargin(30)
                .setSequence(true)
                .setDismissOnTouch(true)
                .setContentTextColor(resources.getColor(R.color.colorforoffwhite))
                .setMaskColour(resources.getColor(R.color.coloryellow))
                .setDelay(i) // optional but starting animations immediately in onCreate can make them choppy
                .singleUse(SHOWCASE_ID) // provide a unique ID used to ensure it is only shown once
                .show()
    }

    private fun setSP(context: Context) {
        try {
            if (SPVariables.getString("Resolution", context) == null || SPVariables.getString("Resolution", context).isEmpty()) {
                SPVariables.setString("Resolution", "720P", context)
            }
            if (SPVariables.getString("Quality", context) == null || SPVariables.getString("Quality", context).isEmpty()) {
                SPVariables.setString("Quality", "HD", context)
            }
            if (SPVariables.getString("FPS", context) == null || SPVariables.getString("FPS", context).isEmpty()) {
                SPVariables.setString("FPS", "60FPS", context)
            }
            if (SPVariables.getString("Orientation", context) == null || SPVariables.getString("Orientation", context).isEmpty()) {
                SPVariables.setString("Orientation", "Portrait", context)
            }
            if (SPVariables.getString("CameraFacing", context) == null || SPVariables.getString("CameraFacing", context).isEmpty()) {
                SPVariables.setString("CameraFacing", "Front", context)
            }
            if (SPVariables.getString("CameraFrame", context) == null || SPVariables.getString("CameraFrame", context).isEmpty()) {
                SPVariables.setString("CameraFrame", "Round", context)
            }
            if (SPVariables.getString("CameraPreview", context) == null || SPVariables.getString("CameraPreview", context).isEmpty()) {
                SPVariables.setString("CameraPreview", "Medium", context)
            }
            if (SPVariables.getString("RecordAudio", context) == null || SPVariables.getString("RecordAudio", context).isEmpty()) {
                SPVariables.setString("RecordAudio", "TRUE", context)
            }
            if (SPVariables.getString("CountDown", context) == null || SPVariables.getString("CountDown", context).isEmpty()) {
                SPVariables.setString("CountDown", "TRUE", context)
            }
            if (SPVariables.getString("ShowBubble", context) == null || SPVariables.getString("ShowBubble", context).isEmpty()) {
                SPVariables.setString("ShowBubble", "TRUE", context)
            }
            if (SPVariables.getString("RecordStartOrStop", context) == null || SPVariables.getString("RecordStartOrStop", context).isEmpty()) {
                SPVariables.setString("RecordStartOrStop", "NOTSTARTED", context)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun CheckingPermissionIsEnabledOrNot(): Boolean {
        val FirstPermissionResult = ContextCompat.checkSelfPermission(applicationContext, permission.CAMERA)
        val SecondPermissionResult = ContextCompat.checkSelfPermission(applicationContext, permission.RECORD_AUDIO)
        val ThirdPermissionResult = ContextCompat.checkSelfPermission(applicationContext, permission.WRITE_EXTERNAL_STORAGE)
        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED && SecondPermissionResult == PackageManager.PERMISSION_GRANTED && ThirdPermissionResult == PackageManager.PERMISSION_GRANTED
    }

    private fun RequestMultiplePermission() {
        // Creating String Array with Permissions.
        ActivityCompat.requestPermissions(this@DashboardActivity, arrayOf(
                permission.CAMERA,
                permission.RECORD_AUDIO,
                permission.WRITE_EXTERNAL_STORAGE
        ), RequestPermissionCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == PermissionManager.REQUEST_SETTINGS_CODE) {
            // User returned from settings, check permissions again
            if (permissionManager.checkAndRequestPermissions()) {
                initializeApp()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        // Handle permission results through PermissionManager
        permissionManager.handlePermissionResult(
            requestCode,
            permissions,
            grantResults,
            onAllGranted = {
                // All permissions granted, initialize the app
                initializeApp()
            },
            onDenied = {
                // Handle permission denied
                Toast.makeText(this, "Required permissions not granted", Toast.LENGTH_SHORT).show()
            }
        )
    }

    var startActivityIntent = registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val i = Intent(this, DashboardActivity::class.java)
            startActivity(i)
            finish()
            if (!firstStart) {
                finish()
            }
        }
    }

    companion object {
        const val PREF_KEY_FIRST_START = "PREF_KEY_FIRST_START"
        const val REQUEST_CODE_SETTING = 1
        private const val CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084
        const val RequestPermissionCode = 7
        fun getResolution(path: String?): String {
            var Resolution = ""
            val metaRetriever = MediaMetadataRetriever()
            metaRetriever.setDataSource(path)
            val height = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
            val width = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
            Resolution = "$width x $height"
            return Resolution
        }

        fun gethms(millis: Long?): String {
            val h = String.format("%02d", TimeUnit.MILLISECONDS.toHours(millis!!))
            val m = String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)))
            val s = String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)))
            var hms: String? = ""
            return if (h == "00") {
                m + ":" + s.also { hms = it }
            } else {
                h + ":" + m + ":" + s.also { hms = it }
            }
        }

        @Throws(IOException::class)
        fun checkVideoDurationValidation(context: Context?, uri: Uri?): Long {
            try {
                val retriever = MediaMetadataRetriever()
                //use one of overloaded setDataSource() functions to set your data source
                retriever.setDataSource(context, uri)
                val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                val timeInMillisec = time!!.toLong()
                retriever.release()
                return timeInMillisec
            } catch (e: Exception) {
                Log.e("checkVideoDurationValidation", e.printStackTrace().toString() )
                return 0L
            }
        }
    }
}
