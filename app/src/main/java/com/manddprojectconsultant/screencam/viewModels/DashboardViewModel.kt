package com.manddprojectconsultant.screencam.viewModels

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.AndroidViewModel
import com.manddprojectconsultant.screencam.R
import com.manddprojectconsultant.screencam.activity.DashboardActivity
import com.manddprojectconsultant.screencam.model.VideoModel
import com.manddprojectconsultant.screencam.service.FloatingViewService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.DecimalFormat
import java.util.*
import kotlin.coroutines.CoroutineContext

class DashboardViewModel(application: Application? = null):
        AndroidViewModel(application!!) {
    private val parentJob = Job()
    private val coroutineContext: CoroutineContext get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    fun loadVideos(context: Context,
                   listener: (List<VideoModel>) -> Unit,
    ) {
        scope.launch {
            val isServiceRunning = isMyServiceRunning(context, FloatingViewService::class.java)
            val videoList: ArrayList<VideoModel> = arrayListOf()
            try {
                val lstVideoModel = ArrayList<VideoModel>()
                val folderName = context.resources.getString(R.string.main_folder_name)
                val mainFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).toString() + File.separator + folderName)
                val file = File(mainFile, "Recording")
                val temp_file = File(mainFile, ".temp")
                if (!temp_file.exists()) temp_file.mkdirs()
                val files = file.listFiles()
                if (files != null && files.size > 0) {
                    for (i in files.indices) {
                        val f = files[i]
                        Log.e("getVideoList", "File name: " + f.name)
                        val fileName = f.name
                        val FName = fileName.substring(0, fileName.length - 4) + ".jpg"
                        val tf = File(temp_file, FName)
                        val videoModel = VideoModel()
                        if (f.name.endsWith(".mp4") && f.length() > 0) {
                            videoModel.vPath = f.path
                            val size = f.length()
                            videoModel.vSize = size(size)
                            videoModel.vDuration = DashboardActivity.gethms(DashboardActivity.checkVideoDurationValidation(context, Uri.parse(f.path)))
                            videoModel.vName = f.name
                            videoModel.vResolution = DashboardActivity.getResolution(f.path)
                            lstVideoModel.add(videoModel)
                        }
                        if (f.name.startsWith("SC_") && f.name.endsWith(".mp4") && f.length() == 0L) {
                            f.delete()
                        }
                    }
                    Collections.sort(lstVideoModel) { lhs, rhs -> rhs.getvName().compareTo(lhs.getvName()) }
//                    val lstVideoName = ArrayList<String>()
//                    for (vm in lstVideoModel) {
//                        lstVideoName.add(vm.getvName())
//                    }
                }
                listener.invoke(lstVideoModel)
            } catch (e: IOException) {
                throw RuntimeException(e)
            }

        }
    }
    private fun isMyServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    fun size(size: Long): String {
        val df = DecimalFormat("0.00")
        val sizeKb = 1000.0f
        val sizeMb = sizeKb * sizeKb
        val sizeGb = sizeMb * sizeKb
        val sizeTerra = sizeGb * sizeKb
        if (size < sizeMb) return df.format((size / sizeKb).toDouble()) + " kB" else if (size < sizeGb) return df.format((size / sizeMb).toDouble()) + " MB" else if (size < sizeTerra) return df.format((size / sizeGb).toDouble()) + " GB"
        return ""
    }

}