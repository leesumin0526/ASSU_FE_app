package com.ssu.assu.presentation.admin.dashboard

import android.graphics.Canvas
import android.graphics.RectF
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler

class RoundedBarChartRenderer(
    chart: BarDataProvider,
    animator: ChartAnimator,
    viewPortHandler: ViewPortHandler
) : BarChartRenderer(chart, animator, viewPortHandler) {

    private val mRadius = 12f

    override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {
        val trans = mChart.getTransformer(dataSet.axisDependency)

        mRenderPaint.color = dataSet.color
        mRenderPaint.alpha = 255

        val phaseY = mAnimator.phaseY

        for (j in 0 until dataSet.entryCount) {
            val e = dataSet.getEntryForIndex(j)

            if (!mViewPortHandler.isInBoundsLeft(e.x + 0.5f)) continue
            if (!mViewPortHandler.isInBoundsRight(e.x - 0.5f)) break

            val barData = mChart.barData
            val barWidth = barData.barWidth

            val left = e.x - barWidth / 2f
            val right = e.x + barWidth / 2f
            var top = if (e.y >= 0) e.y else 0f
            var bottom = if (e.y <= 0) e.y else 0f

            top *= phaseY
            bottom *= phaseY

            val barRect = RectF(left, top, right, bottom)
            trans.rectValueToPixel(barRect)

            c.drawRoundRect(barRect, mRadius, mRadius, mRenderPaint)
        }
    }
}