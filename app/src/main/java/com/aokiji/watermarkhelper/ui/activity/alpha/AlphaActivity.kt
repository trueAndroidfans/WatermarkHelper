package com.aokiji.watermarkhelper.ui.activity.alpha

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.aokiji.watermarkhelper.R
import com.aokiji.watermarkhelper.ui.activity.main.MainActivity
import kotlinx.android.synthetic.main.activity_alpha.*

class AlphaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alpha)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            setFullScreenStyle()
        }

        initView()
    }


    private fun initView() {
        logo.postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 3000)
    }


    @RequiresApi(Build.VERSION_CODES.P)
    private fun setFullScreenStyle() {
        window.apply {
            attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
    }

}