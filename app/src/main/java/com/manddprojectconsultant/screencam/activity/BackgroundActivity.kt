package com.manddprojectconsultant.screencam.activity

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import com.manddprojectconsultant.screencam.R
import com.manddprojectconsultant.screencam.databinding.ActivityBackgroundBinding
import com.manddprojectconsultant.screencam.service.FloatingViewService

class BackgroundActivity : AppCompatActivity() {
    
    lateinit var binding: ActivityBackgroundBinding
    var context: Context = this@BackgroundActivity
    var floatingViewService: FloatingViewService? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBackgroundBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val expandedView =
            FloatingViewService.mFloatingViewExpand.findViewById<View>(R.id.expanded_container)
        if (expandedView.isGone) {
            this.finish()
        }
        binding.rootBGView.setOnClickListener {
            floatingViewService?.expandedView?.visibility = View.GONE
            binding.rootBGView.setBackgroundColor(resources.getColor(R.color.transpermt))
            finish()
        }

        FloatingViewService.backgroundActivity = this
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()

        if (floatingViewService?.expandedView != null) {
            floatingViewService?.expandedView?.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        floatingViewService?.expandedView?.visibility = View.GONE
        binding.rootBGView.setBackgroundColor(resources.getColor(R.color.transpermt))
    }
}
