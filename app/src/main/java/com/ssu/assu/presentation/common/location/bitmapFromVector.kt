package com.ssu.assu.presentation.common.location

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import kotlin.math.max

fun vectorToBitmap(context: Context, @DrawableRes resId: Int): Bitmap {
    val d = AppCompatResources.getDrawable(context, resId)!!
    val defaultPx = (24 * context.resources.displayMetrics.density).toInt()
    val w = max(d.intrinsicWidth, defaultPx)
    val h = max(d.intrinsicHeight, defaultPx)

    val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    d.setBounds(0, 0, canvas.width, canvas.height)
    d.draw(canvas)
    return bmp
}

