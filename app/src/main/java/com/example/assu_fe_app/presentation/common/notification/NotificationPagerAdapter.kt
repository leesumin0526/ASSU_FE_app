package com.example.assu_fe_app.presentation.common.notification

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.assu_fe_app.presentation.common.notification.NotificationUnreadFragment

class NotificationPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(
    activity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return (if (position == 0) {
            NotificationAllFragment()
        } else {
            NotificationUnreadFragment()
        }) as Fragment
    }
}