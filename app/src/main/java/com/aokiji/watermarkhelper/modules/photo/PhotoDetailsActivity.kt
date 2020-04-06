package com.aokiji.watermarkhelper.modules.photo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.aokiji.watermarkhelper.R
import com.aokiji.watermarkhelper.Settings
import com.aokiji.watermarkhelper.adapter.PhotoViewsAdapter
import com.aokiji.watermarkhelper.models.SummaryItem
import kotlinx.android.synthetic.main.activity_photo_details.*

class PhotoDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_details)

        init()
    }


    private fun init() {
        val summaryItem = intent.getSerializableExtra(Settings.INTENT_KEY_IMAGES) as SummaryItem
        vpPhoto.apply {
            adapter = PhotoViewsAdapter(context, summaryItem.images)
            registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                    tvNumber.text = String.format("%s/%s", position + 1, summaryItem.images.size)
                }
            })
        }
    }
}
