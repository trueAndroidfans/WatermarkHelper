package com.aokiji.watermarkhelper.base

import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_main.*

open class ToolbarActivity : AppCompatActivity() {

    protected fun setupToolbar(toolbar: Toolbar, titleId: Int, allowReturn: Boolean) {
        toolBar.setTitle(titleId)
        setSupportActionBar(toolbar)
        if (allowReturn) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }

        return super.onOptionsItemSelected(item)
    }
}