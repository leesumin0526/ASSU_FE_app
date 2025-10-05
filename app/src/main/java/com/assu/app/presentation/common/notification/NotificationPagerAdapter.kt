package com.assu.app.presentation.common.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class NotificationPagerAdapter(
    fa: FragmentActivity,
    private val role: NotificationActivity.Role
) : FragmentStateAdapter(fa) {

    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        val args = Bundle().apply { putSerializable("arg_role", role) }
        return when (position) {
            0 -> NotificationAllFragment().apply { arguments = args }
            else -> NotificationUnreadFragment().apply { arguments = args }
        }
    }
}