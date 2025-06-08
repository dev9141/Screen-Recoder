package com.manddprojectconsultant.screencam.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PermissionManager(private val activity: Activity) {
    companion object {
        const val REQUEST_PERMISSION_CODE = 1001
        const val REQUEST_SETTINGS_CODE = 1002
        
        // Basic permissions needed for all Android versions
        private val BASIC_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        
        // Additional permissions for Android 13+ (API 33+)
        private val ANDROID_13_PERMISSIONS = arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO
        )

        // Additional permissions for Android 14+ (API 34+)
        private val ANDROID_14_PERMISSIONS = arrayOf(
            Manifest.permission.FOREGROUND_SERVICE_MEDIA_PROJECTION
        )
        
        // Get all required permissions based on Android version
        fun getRequiredPermissions(): Array<String> {
            return when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                    // For Android 14 and above
                    BASIC_PERMISSIONS.filter { it != Manifest.permission.WRITE_EXTERNAL_STORAGE }.toTypedArray() +
                    ANDROID_13_PERMISSIONS +
                    ANDROID_14_PERMISSIONS
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                    // For Android 13 and above
                    BASIC_PERMISSIONS.filter { it != Manifest.permission.WRITE_EXTERNAL_STORAGE }.toTypedArray() +
                    ANDROID_13_PERMISSIONS
                }
                else -> {
                    BASIC_PERMISSIONS
                }
            }
        }
    }

    fun checkAndRequestPermissions(): Boolean {
        val permissions = getRequiredPermissions()
        val notGrantedPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
        }

        return if (notGrantedPermissions.isEmpty()) {
            true // All permissions are granted
        } else {
            // Request permissions that are not granted
            ActivityCompat.requestPermissions(
                activity,
                notGrantedPermissions.toTypedArray(),
                REQUEST_PERMISSION_CODE
            )
            false
        }
    }

    fun handlePermissionResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
        onAllGranted: () -> Unit,
        onDenied: () -> Unit
    ) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            
            if (allGranted) {
                onAllGranted()
            } else {
                // Check if we should show the rationale
                val shouldShowRationale = permissions.any {
                    ActivityCompat.shouldShowRequestPermissionRationale(activity, it)
                }

                if (shouldShowRationale) {
                    showPermissionRationaleDialog()
                } else {
                    // User selected "Never ask again"
                    showSettingsDialog()
                }
                onDenied()
            }
        }
    }

    private fun showPermissionRationaleDialog() {
        MaterialAlertDialogBuilder(activity)
            .setTitle("Permissions Required")
            .setMessage("This app needs the requested permissions to function properly. Please grant them in the next screen.")
            .setPositiveButton("Grant") { _, _ ->
                checkAndRequestPermissions()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showSettingsDialog() {
        MaterialAlertDialogBuilder(activity)
            .setTitle("Permissions Required")
            .setMessage("Some permissions are permanently denied. Please enable them in Settings to use this app.")
            .setPositiveButton("Settings") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun openAppSettings() {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", activity.packageName, null)
            activity.startActivityForResult(this, REQUEST_SETTINGS_CODE)
        }
    }
} 