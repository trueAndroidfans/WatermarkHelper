package com.aokiji.watermarkhelper.ui.activity.main

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.aokiji.watermarkhelper.R
import com.aokiji.watermarkhelper.Settings
import com.aokiji.watermarkhelper.ui.adapter.SummaryAdapter
import com.aokiji.watermarkhelper.base.ToolbarActivity
import com.aokiji.watermarkhelper.models.entities.Image
import com.aokiji.watermarkhelper.models.entities.SummaryItem
import com.aokiji.watermarkhelper.ui.activity.add.AddWatermarkActivity
import com.aokiji.watermarkhelper.ui.activity.photo.PhotoDetailsActivity
import com.aokiji.watermarkhelper.ui.activity.setting.SettingActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_add_watermark.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.toolBar
import java.io.File

class MainActivity : ToolbarActivity() {

    private val prosecutor = RxPermissions(this)

    private val list: MutableList<SummaryItem> = mutableListOf()
    private val images: MutableList<Image> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()

        requestThePermission()
    }


    override fun onRestart() {
        super.onRestart()

        initData()
    }


    private fun initView() {
        setupToolbar(toolBar, R.string.app_name, false)

        initRefreshLayout()

        initRecyclerView()
    }


    private fun initRefreshLayout() {
        refreshLayout.setColorSchemeResources(R.color.colorOnly, R.color.color_Only_40)
        refreshLayout.setOnRefreshListener {
            initData()
        }
    }


    private fun initRecyclerView() {
        val layoutManager = GridLayoutManager(this, 2)
        layoutManager.orientation = GridLayoutManager.VERTICAL
        rvMain.layoutManager = layoutManager
        rvMain.adapter = SummaryAdapter(this, list) {
            val intent = Intent(MainActivity@ this, PhotoDetailsActivity::class.java)
            intent.putExtra(Settings.INTENT_KEY_IMAGES, list[it])
            startActivity(intent)
        }
    }


    private fun requestThePermission() {
        prosecutor.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe {
            if (it) {
                refreshLayout.isEnabled = true
                initData()
            } else {
                refreshLayout.isEnabled = false
                showError(Settings.TYPE_PERMISSION_ERROR, Throwable("you denied the permission"))
            }
        }
    }


    private fun initData() {
        val directory = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.path)
        val files = directory.listFiles()
        images.clear()
        if (files != null && files.isNotEmpty()) {
            for (it in files) {
                val list = it.name.split("_")
                if (list.isNotEmpty()) {
                    val image = Image(list[0], list[1], it.path)
                    images.add(image)
                }
            }
        }
        if (images.isNotEmpty()) {
            val map: MutableMap<String, MutableList<Image>> = mutableMapOf()
            for (it in images) {
                var temp: MutableList<Image>?
                val key = it.lastName
                temp = map[key]
                if (temp == null) {
                    temp = mutableListOf()
                    map[key] = temp
                }
                temp.add(it)
            }
            if (map.isNotEmpty()) {
                list.clear()
                for (it in map.keys) {
                    list.add(SummaryItem(map[it]!!, map[it]!!.size, it))
                }
            }
            tvEmptyData.visibility = View.GONE
        }
        rvMain.visibility = View.VISIBLE
        tvError.visibility = View.GONE
        rvMain.adapter?.notifyDataSetChanged()

        if (refreshLayout.isRefreshing) refreshLayout.isRefreshing = false
    }


    private fun showError(type: Int, errorMessage: Throwable) {
        rvMain.visibility = View.GONE
        tvError.visibility = View.VISIBLE
        tvError.text = errorMessage.message
        tvError.setOnClickListener {
            when (type) {
                Settings.TYPE_PERMISSION_ERROR -> requestThePermission()
                Settings.TYPE_DATA_ERROR -> initData()
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add -> {
                startActivity(Intent(MainActivity@ this, AddWatermarkActivity::class.java))
            }
            R.id.action_setting -> {
                startActivity(Intent(MainActivity@ this, SettingActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
