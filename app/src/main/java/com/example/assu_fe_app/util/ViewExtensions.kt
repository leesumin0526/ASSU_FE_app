package com.example.assu_fe_app.util


import android.animation.ValueAnimator
import android.view.ViewGroup
import android.widget.ImageView

fun ImageView.setProgressBarFillAnimated(
    container: ViewGroup,
    fromPercent: Float,
    toPercent: Float,
    duration: Long = 300L
) {
    container.post {
        val totalWidth = container.width
        val startWidth = (totalWidth * fromPercent).toInt()
        val endWidth = (totalWidth * toPercent).toInt()

        val animator = ValueAnimator.ofInt(startWidth, endWidth)
        animator.duration = duration

        animator.addUpdateListener { animation ->
            val animatedWidth = animation.animatedValue as Int
            val params = this.layoutParams
            params.width = animatedWidth
            this.layoutParams = params
        }

        animator.start()
    }
}