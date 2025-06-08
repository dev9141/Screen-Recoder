package com.manddprojectconsultant.screencam.activity

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.manddprojectconsultant.screencam.R
import com.manddprojectconsultant.screencam.adapter.SliderAdapter
import com.manddprojectconsultant.screencam.utils.AppUpdateManager
import com.manddprojectconsultant.screencam.utils.PreferenceManager
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.InstallStatus
import kotlinx.coroutines.launch

class WelcomeActivity : AppCompatActivity() {
    private var viewPager: ViewPager? = null
    private var dotsLayout: LinearLayout? = null
    private var sliderAdapter: SliderAdapter? = null
    private lateinit var dots: Array<TextView?>
    private var letsGetStarted: Button? = null
    private var btnskip: Button? = null
    private var btnnext: ImageView? = null
    private var animation: Animation? = null
    private var currentPos = 0
    private var tvmadeinindia: TextView? = null
    private var preferenceManager: PreferenceManager? = null
    private lateinit var appUpdateManager: AppUpdateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_welcome)
        
        preferenceManager = PreferenceManager(this)
        appUpdateManager = AppUpdateManager(this)
        
        if (!preferenceManager!!.isFirstTimeLaunch) {
            restorePrefData()
            finish()
        }
        
        init()
        sliderAdapter = SliderAdapter(this)
        viewPager!!.adapter = sliderAdapter
        addDots(0)
        viewPager!!.addOnPageChangeListener(changeListener)
    }

    private fun init() {
        viewPager = findViewById(R.id.slider)
        dotsLayout = findViewById(R.id.dots)
        letsGetStarted = findViewById(R.id.get_started_btn)
        btnskip = findViewById(R.id.btn_skip)
        btnnext = findViewById(R.id.btnnext)
        tvmadeinindia = findViewById(R.id.tvmadeinindia)
    }

    private fun restorePrefData() {
        preferenceManager!!.isFirstTimeLaunch = false
        startActivity(Intent(this@WelcomeActivity, DashboardActivity::class.java))
        finish()
    }

    private fun addDots(position: Int) {
        dots = arrayOfNulls(3)
        dotsLayout!!.removeAllViews()
        for (i in dots.indices) {
            dots[i] = TextView(this)
            dots[i]!!.text = Html.fromHtml("&#8226")
            dots[i]!!.textSize = 35f
            dots[i]!!.setTextColor(resources.getColor(R.color.videoListStrip))
            dotsLayout!!.addView(dots[i])
        }
        if (dots.size > 0) {
            dots[position]!!.setTextColor(resources.getColor(R.color.coloryellow))
        }
    }

    var changeListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
        override fun onPageSelected(position: Int) {
            addDots(position)
            currentPos = position
            if (position == 0) {
                letsGetStarted!!.visibility = View.INVISIBLE
                tvmadeinindia!!.visibility = View.VISIBLE
            } else if (position == 1) {
                letsGetStarted!!.visibility = View.INVISIBLE
                tvmadeinindia!!.visibility = View.GONE
            } else {
                animation = AnimationUtils.loadAnimation(this@WelcomeActivity, R.anim.button_animation)
                letsGetStarted!!.animation = animation
                letsGetStarted!!.visibility = View.VISIBLE
                tvmadeinindia!!.visibility = View.GONE
            }
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }

    fun skip(view: View?) {
        startActivity(Intent(this, DashboardActivity::class.java))
        preferenceManager!!.isFirstTimeLaunch = false
        lifecycleScope.launch {
            appUpdateManager.checkForUpdates(this@WelcomeActivity)
        }
        finish()
    }

    fun next(view: View?) {
        if (letsGetStarted!!.visibility == View.INVISIBLE) {
            viewPager!!.currentItem = currentPos + 1
        } else {
            letgo(view)
        }
    }

    fun letgo(view: View?) {
        startActivity(Intent(this, DashboardActivity::class.java))
        preferenceManager!!.isFirstTimeLaunch = false
        lifecycleScope.launch {
            appUpdateManager.checkForUpdates(this@WelcomeActivity)
        }
        finish()
    }
}