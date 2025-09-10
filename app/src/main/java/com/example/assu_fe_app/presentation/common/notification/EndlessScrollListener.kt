package com.example.assu_fe_app.presentation.common.notification

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class EndlessScrollListener : RecyclerView.OnScrollListener() {
    override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
        if (dy <= 0) return
        val lm = rv.layoutManager as? LinearLayoutManager ?: return
        if (lm.findLastVisibleItemPosition() >= (rv.adapter?.itemCount ?: 0) - 4) {
            onLoadMore()
        }
    }
    abstract fun onLoadMore()
}