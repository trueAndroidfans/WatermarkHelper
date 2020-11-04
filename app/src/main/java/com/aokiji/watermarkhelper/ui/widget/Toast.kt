package com.aokiji.watermarkhelper.ui.widget

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.LayoutRes
import com.aokiji.watermarkhelper.R

object Toast {

    fun e(context: Context, text: String) {
        create(context, R.layout.view_toast_e, text).show()
    }


    private fun create(context: Context, @LayoutRes resId: Int, text: String): Toast {
        val toast = Toast(context)
        val view = LayoutInflater.from(context).inflate(resId, null)
        val tvText = view.findViewById<TextView>(R.id.tv_text)
        tvText.text = text
        toast.view = view
        toast.setGravity(Gravity.BOTTOM, 0, 15)
        return toast
    }

}