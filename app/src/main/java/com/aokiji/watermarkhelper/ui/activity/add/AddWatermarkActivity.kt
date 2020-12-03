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
import android.text.StaticLayout
import android.text.TextPaint
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.content.FileProvider
import com.aokiji.watermarkhelper.R
import com.aokiji.watermarkhelper.Settings
import com.aokiji.watermarkhelper.base.ToolbarActivity
import com.aokiji.watermarkhelper.models.entities.Parameter
import com.aokiji.watermarkhelper.ui.widget.Toast
import com.aokiji.watermarkhelper.utils.sp2px
import com.bumptech.glide.Glide
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

    private lateinit var imageFile: File
    private lateinit var imageUri: Uri

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
    }


    private fun openCamera() {
        imageFile = File(externalCacheDir, "output_img.jpg")
        if (imageFile.exists()) imageFile.delete()
        imageFile.createNewFile()
        imageUri = if (Build.VERSION.SDK_INT >= 24) FileProvider.getUriForFile(this, "com.aokiji.watermarkhelper.fileprovider", imageFile)
        else Uri.fromFile(imageFile)
        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, Settings.INTENT_KEY_TAKE_PHOTO)
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
                    ivEmpty.visibility = View.GONE
                    val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))
                    ivPicture.setImageBitmap(bitmap)
                    imagePath = imageFile.path
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
        ivEmpty.visibility = View.GONE
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_watermark, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_camera -> openCamera()
            R.id.action_picture -> openAlbum()
            R.id.action_done -> if (imagePath.isNullOrEmpty()) Toast.e(this, "Please choose image.") else addWatermark()
        }
        return super.onOptionsItemSelected(item)
    }


    private fun addWatermark() {
        prosecutor.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe {
            if (it) {
                Observable.just(imagePath)
                        .map { path -> create(path) }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { newPath ->
                            Toast.e(this, newPath)
                            finish()
                        }
            } else {
                Toast.e(this, "allow permission please!")
            }
        }
    }


    private fun create(imagePath: String?): String {
        val bitmap = BitmapFactory.decodeFile(imagePath).copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(bitmap)
        val parameter = generateWatermarkParameter()
        val textPaint = TextPaint().apply {
            isAntiAlias = true
            isDither = true
            isFilterBitmap = true
            color = parameter.textColor
            textSize = sp2px(this@AddWatermarkActivity, parameter.size.toFloat())
        }
        val staticLayout = StaticLayout.Builder.obtain(parameter.waterMark, 0, parameter.waterMark.length, textPaint, bitmap.width).build()
        canvas.translate(0f, bitmap.height - staticLayout.height.toFloat())
        staticLayout.draw(canvas)

        return generateFile(bitmap).path
    }


    private fun generateWatermarkParameter(): Parameter {
        val textColor: Int =
                if (mmkv.decodeString(Settings.MMKV_KEY_WATERMARK_COLOR).isNullOrEmpty())
                    Color.parseColor("#7A7A7A")
                else
                    Color.parseColor(mmkv.decodeString(Settings.MMKV_KEY_WATERMARK_COLOR))
        val textSize: Int =
                if (mmkv.decodeString(Settings.MMKV_KEY_WATERMARK_TEXT_SIZE).isNullOrEmpty())
                    8
                else
                    mmkv.decodeString(Settings.MMKV_KEY_WATERMARK_TEXT_SIZE).split(" ")[0].toInt()
        val waterMark = getString(R.string.text_watermark_title) + mmkv.decodeString(Settings.MMKV_KEY_WATERMARK)
        return Parameter(textColor, textSize, waterMark)
    }


    private fun generateFile(bitmap: Bitmap): File {
        val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Date()) + ".jpg")
        val bufferedOutputStream = BufferedOutputStream(FileOutputStream(file))
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bufferedOutputStream)
        if (bitmap != null && !bitmap.isRecycled) bitmap.recycle()
        bufferedOutputStream.flush()
        bufferedOutputStream.close()

        return file
    }

}
