package com.example.assu_fe_app.presentation.common.notification

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentNotificationUnreadBinding
import com.example.assu_fe_app.presentation.base.BaseFragment

class NotificationUnreadFragment : BaseFragment<FragmentNotificationUnreadBinding>(R.layout.fragment_notification_unread) {
    override fun initView() {
        initRecyclerView()

    }

    override fun initObserver() {

    }

    private fun initRecyclerView() {

        // dummy data
        val dummyData = listOf(
            NotificationItem(
                "주문 안내",
                "9번 테이블에서 제로콜라 혜택을 선택하셨어요",
                "10분전",
                false
            ),
            NotificationItem(
                "주문 안내",
                "19번 테이블에서 제로콜라 혜택을 선택하셨어요",
                "10분전",
                false
            )
        )
        val adapter = NotificationAdapter(dummyData)
        binding.rvNotificationUnread.apply {
            layoutManager = LinearLayoutManager(requireContext()) // 세로 스크롤
            this.adapter = adapter
            setHasFixedSize(true) // 아이템 크기 고정 시 성능 최적화
        }
    }

}