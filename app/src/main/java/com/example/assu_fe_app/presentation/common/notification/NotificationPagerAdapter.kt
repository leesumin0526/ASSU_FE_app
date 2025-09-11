package com.example.assu_fe_app.presentation.common.notification

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.assu_fe_app.presentation.common.notification.NotificationUnreadFragment

class NotificationPagerAdapter(
    fa: FragmentActivity,
    private val role: NotificationActivity.Role
) : FragmentStateAdapter(fa) {

    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment =
        if (position == 0) NotificationAllFragment.newInstance(role)
        else NotificationUnreadFragment.newInstance(role)
}