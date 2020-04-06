package com.aokiji.watermarkhelper.modules.setting.edit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.aokiji.watermarkhelper.R
import com.aokiji.watermarkhelper.Settings
import com.aokiji.watermarkhelper.base.ToolbarActivity
import kotlinx.android.synthetic.main.activity_edit.*

class EditActivity : ToolbarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        initView()

        initData()
    }


    private fun initView() {
        setupToolbar(toolBar, R.string.title_edit, true)

        ivClean.setOnClickListener { etContent.setText("") }
    }


    private fun initData() {
        etContent.setText(intent.getStringExtra(Settings.INTENT_KEY_INPUT_CONTENT))
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> {
                val intent = Intent()
                intent.putExtra(Settings.INTENT_KEY_OUTPUT_CONTENT, etContent.text.toString())
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
