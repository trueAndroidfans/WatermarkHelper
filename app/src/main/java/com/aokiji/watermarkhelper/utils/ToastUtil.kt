package com.aokiji.watermarkhelper.utils

import android.content.Context
import android.widget.Toast

fun showMsg(context: Context, msg: String?) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}

fun showMsg(context: Context, msgId: Int) {
    Toast.makeText(context, msgId, Toast.LENGTH_SHORT).show()
}