package com.assu.app.presentation.user.home

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class TransparentHoleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val backgroundPaint = Paint().apply {
        color = 0xAA000000.toInt()  // 반투명 검정
    }

    private val clearPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    private var holeRect = RectF(0f, 0f, 0f, 0f)

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }

    fun updateHoleRectFromView(targetView: View) {
        val location = IntArray(2)
        targetView.getLocationOnScreen(location)

        val x = location[0].toFloat()
        val y = location[1].toFloat()-90
        val width = targetView.width.toFloat()
        val height = targetView.height.toFloat()

        holeRect = RectF(x, y, x + width, y + height)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)
        canvas.drawRoundRect(holeRect, 64f, 64f, clearPaint)
    }
}
