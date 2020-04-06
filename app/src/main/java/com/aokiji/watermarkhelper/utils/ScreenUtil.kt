package com.aokiji.watermarkhelper.utils

import android.content.Context
import android.util.TypedValue

fun sp2px(context: Context, value: Float) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, context.resources.displayMetrics)