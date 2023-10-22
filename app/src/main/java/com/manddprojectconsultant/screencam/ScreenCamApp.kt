package com.manddprojectconsultant.screencam

import android.app.Application
import com.manddprojectconsultant.screencam.utils.PreferenceManager

class ScreenCamApp: Application() {
    lateinit var preferenceManager: PreferenceManager
    override fun onCreate() {
        super.onCreate()

    }

    init {
        instance = this
//        preferenceManager = PrefManager(this);
    }

    companion object{
        lateinit var instance: ScreenCamApp
    }
}