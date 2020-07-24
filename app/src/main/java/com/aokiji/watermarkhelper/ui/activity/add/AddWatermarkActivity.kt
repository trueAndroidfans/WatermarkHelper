package com.aokiji.watermarkhelper.ui.activity.add

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.ContentUris
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.aokiji.watermarkhelper.R
import com.aokiji.watermarkhelper.Settings
import com.aokiji.watermarkhelper.base.ToolbarActivity
import com.aokiji.watermarkhelper.utils.showMsg
import com.aokiji.watermarkhelper.utils.sp2px
import com.bumptech.glide.Glide
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton
import com.orhanobut.logger.Logger
import com.tbruyelle.rxpermissions2.RxPermissions
import com.tencent.mmkv.MMKV
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_watermark.*
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class AddWatermarkActivity : ToolbarActivity() {

    private val prosecutor = RxPermissions(this)

    private var imagePath: String? = ""

    private lateinit var mmkv: MMKV

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_watermark)

        initMMKV()

        initView()
    }


    private fun initMMKV() {
        mmkv = MMKV.defaultMMKV()
    }


    private fun initView() {
        setupToolbar(toolBar, R.string.title_add_watermark, true)

        initCircularFloatingActionMenu()
    }


    private fun initCircularFloatingActionMenu() {
        val icon = ImageView(this)
        icon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_add))
        val actionButton = FloatingActionButton.Builder(this).setContentView(icon).build()
        val itemBuilder = SubActionButton.Builder(this)
        val picture = ImageView(this)
        picture.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_picture))
        val pictureButton = itemBuilder.setContentView(picture).build()
        val camera = ImageView(this)
        camera.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_camera))
        val cameraButton = itemBuilder.setContentView(camera).build()
        val menu = FloatingActionMenu.Builder(this)
                .addSubActionView(pictureButton)
                .addSubActionView(cameraButton)
                .attachTo(actionButton)
                .build()
        cameraButton.setOnClickListener {
            menu.close(true)
            showMsg(this, "please wait.")
        }
        pictureButton.setOnClickListener {
            menu.close(true)
            openAlbum()
        }
    }


    private fun openCamera() {

    }


    private fun openAlbum() {
        val intent = Intent("android.intent.action.GET_CONTENT")
        intent.type = "image/*"
        startActivityForResult(intent, Settings.INTENT_KEY_CHOSE_PHOTO)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Settings.INTENT_KEY_TAKE_PHOTO -> {

                }
                Settings.INTENT_KEY_CHOSE_PHOTO -> {
                    if (Build.VERSION.SDK_INT >= 19) handleImageOnKitKat(data) else handleImageBeforeKitKat(data)
                }
            }
        }
    }


    @TargetApi(19)
    private fun handleImageOnKitKat(data: Intent?) {
        var imagePath: String? = null
        val uri = data?.data
        if (DocumentsContract.isDocumentUri(this, uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents" == uri!!.authority) {
                val id = docId.split(":").toTypedArray()[1]
                val selection = MediaStore.Images.Media._ID + "=" + id
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection)
            } else if ("com.android.providers.downloads.documents" == uri.authority) {
                val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(docId))
                imagePath = getImagePath(contentUri, null)
            }
        } else if ("content".equals(uri!!.scheme, ignoreCase = true)) {
            imagePath = getImagePath(uri, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            imagePath = uri.path
        }
        displayImage(imagePath)
    }


    private fun handleImageBeforeKitKat(data: Intent?) {
        val uri = data?.data
        val imagePath = getImagePath(uri, null)
        displayImage(imagePath)
    }


    private fun getImagePath(uri: Uri?, selection: String?): String? {
        var path: String? = null
        val cursor = contentResolver.query(uri!!, null, selection, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path
    }


    private fun displayImage(imagePath: String?) {
        this.imagePath = imagePath
        Glide.with(this).load(imagePath).into(ivPicture)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_watermark, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_done -> addWatermark()
        }
        return super.onOptionsItemSelected(item)
    }


    private fun addWatermark() {
        prosecutor.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe {
            if (it) {
                Observable.just(imagePath)
                        .map { it1 -> create(it1) }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { it2 -> showMsg(this, it2) }
            } else {
                showMsg(this, "allow permission please!")
            }
        }
    }


    private fun create(imagePath: String?): String {
        val bitmap = BitmapFactory.decodeFile(imagePath).copy(Bitmap.Config.ARGB_8888, true)
        val width = bitmap.width
        val height = bitmap.height
        val canvas = Canvas(bitmap)
        val textColor = mmkv.decodeString(Settings.MMKV_KEY_WATERMARK_COLOR)
        val textSizeString = mmkv.decodeString(Settings.MMKV_KEY_WATERMARK_TEXT_SIZE)
        val size: Int = if (textSizeString.isNullOrEmpty()) 25 else textSizeString.split(" ")[0].toInt()
        val textPaint = TextPaint().apply {
            isAntiAlias = true
            isDither = true
            isFilterBitmap = true
            color = if (textColor.isNullOrEmpty()) Color.parseColor("#7A7A7A") else Color.parseColor(textColor)
            textSize = sp2px(this@AddWatermarkActivity, size.toFloat() * 2)
        }
        val waterMark = mmkv.decodeString(Settings.MMKV_KEY_WATERMARK)
        val text = if (waterMark.isNullOrEmpty()) getString(R.string.app_name) else waterMark
        val staticLayout = StaticLayout.Builder.obtain(text, 0, text.length, textPaint, width).build()
        canvas.translate(0f, height - staticLayout.height.toFloat())
        staticLayout.draw(canvas)
        val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Date()) + ".jpg")
        val bufferedOutputStream = BufferedOutputStream(FileOutputStream(file))
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bufferedOutputStream)
        if (bitmap != null && !bitmap.isRecycled) bitmap.recycle()
        bufferedOutputStream.flush()
        bufferedOutputStream.close()

        return file.path
    }

}
