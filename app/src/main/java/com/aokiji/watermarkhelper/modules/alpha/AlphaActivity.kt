package com.aokiji.watermarkhelper.modules.alpha

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aokiji.watermarkhelper.R
import com.aokiji.watermarkhelper.modules.main.MainActivity
import kotlinx.android.synthetic.main.activity_alpha.*

class AlphaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alpha)

        initView()
    }


    private fun initView() {
        logo.postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 3000)
    }
}