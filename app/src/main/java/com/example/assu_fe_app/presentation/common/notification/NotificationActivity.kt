package com.example.assu_fe_app.presentation.common.notification

import android.content.Context
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.ActivityNotificationBinding
import com.example.assu_fe_app.presentation.base.BaseActivity
import com.google.android.material.tabs.TabLayoutMediator

class NotificationActivity : BaseActivity<ActivityNotificationBinding>(R.layout.activity_notification){

    override fun initView() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val extraPaddingTop = 3 // 8dp 추가
            v.setPadding(
                systemBars.left,
                systemBars.top + extraPaddingTop.dpToPx(v.context),
                systemBars.right,
                0
            )
            insets
        }

        val pagerAdapter = NotificationPagerAdapter(this)
        binding.vpPartnerNoti.adapter = pagerAdapter

        TabLayoutMediator(binding.tlPartnerNoti, binding.vpPartnerNoti) { tab, position ->
            tab.text = if (position == 0) "전체" else "안읽음"
        }.attach()

    }

    override fun initObserver() {

    }
    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

}