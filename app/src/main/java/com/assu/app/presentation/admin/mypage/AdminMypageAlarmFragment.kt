package com.assu.app.presentation.admin.mypage

import androidx.navigation.fragment.findNavController
import com.assu.app.R
import com.assu.app.databinding.FragmentAdminMypageAlarmBinding
import com.assu.app.presentation.base.BaseFragment

class AdminMypageAlarmFragment : BaseFragment<FragmentAdminMypageAlarmBinding>(R.layout.fragment_admin_mypage_alarm) {

    override fun initView() {
        // 뒤로가기 버튼 클릭
        binding.btnAdminAlarmBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun initObserver() {}
}
