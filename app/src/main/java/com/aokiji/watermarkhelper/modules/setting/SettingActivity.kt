package com.aokiji.watermarkhelper.modules.setting

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Spinner
import com.aokiji.watermarkhelper.R
import com.aokiji.watermarkhelper.Settings
import com.aokiji.watermarkhelper.base.ToolbarActivity
import com.aokiji.watermarkhelper.modules.setting.edit.EditActivity
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import com.orhanobut.logger.Logger
import com.tencent.mmkv.MMKV
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : ToolbarActivity(), ColorPickerDialogListener {

    private lateinit var mmkv: MMKV

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        initMMKV()

        initView()
    }


    private fun initMMKV() {
        mmkv = MMKV.defaultMMKV()
    }


    private fun initView() {
        setupToolbar(toolBar, R.string.title_setting, true)

        val waterMark = mmkv.decodeString(Settings.MMKV_KEY_WATERMARK)
        tvWatermark.text = if (waterMark.isNullOrEmpty()) getString(R.string.app_name) else waterMark
        val color = mmkv.decodeString(Settings.MMKV_KEY_WATERMARK_COLOR)
        tvColor.setTextColor(if (color.isNullOrEmpty()) Color.parseColor("#7A7A7A") else Color.parseColor(color))
        tvColor.text = tvWatermark.text
        val textSize = mmkv.decodeString(Settings.MMKV_KEY_WATERMARK_TEXT_SIZE)
        var position = 0
        for (it in resources.getStringArray(R.array.TextSize).indices) {
            if (resources.getStringArray(R.array.TextSize)[it] == textSize) {
                position = it
            }
        }
        spTextSize.setSelection(position)

        rlWatermark.setOnClickListener {
            val intent = Intent(this, EditActivity::class.java)
            intent.putExtra(Settings.INTENT_KEY_INPUT_CONTENT, tvWatermark.text.toString())
            startActivityForResult(intent, Settings.REQUEST_CODE_EDIT_WATERMARK)
        }
        rlColor.setOnClickListener {
            //            ColorPickerDialog.newBuilder()
//                    .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
//                    .setAllowPresets(false)
//                    .setDialogId(0)
//                    .setColor(Color.BLACK)
//                    .setShowAlphaSlider(true)
//                    .show(this)
            ColorPickerDialog.newBuilder()
                    .setColor(Color.BLACK)
                    .show(this)
        }
        spTextSize.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                mmkv.encode(Settings.MMKV_KEY_WATERMARK_TEXT_SIZE, resources.getStringArray(R.array.TextSize)[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Settings.REQUEST_CODE_EDIT_WATERMARK -> {
                    val waterMark = data?.getStringExtra(Settings.INTENT_KEY_OUTPUT_CONTENT)
                    mmkv.encode(Settings.MMKV_KEY_WATERMARK, waterMark)
                    tvWatermark.text = waterMark
                    tvColor.text = tvWatermark.text
                }
            }
        }
    }


    override fun onDialogDismissed(dialogId: Int) {

    }


    override fun onColorSelected(dialogId: Int, color: Int) {
        val color = "#" + Integer.toHexString(color)
        mmkv.encode(Settings.MMKV_KEY_WATERMARK_COLOR, color)
        tvColor.setTextColor(Color.parseColor(color))
    }
}
