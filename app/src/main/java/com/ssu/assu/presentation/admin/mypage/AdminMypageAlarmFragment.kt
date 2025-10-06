package com.ssu.assu.presentation.admin.mypage

import androidx.navigation.fragment.findNavController
import com.ssu.assu.R
import com.ssu.assu.databinding.FragmentAdminMypageAlarmBinding
import com.ssu.assu.presentation.base.BaseFragment

class AdminMypageAlarmFragment : BaseFragment<FragmentAdminMypageAlarmBinding>(R.layout.fragment_admin_mypage_alarm) {

    override fun initView() {
        // 뒤로가기 버튼 클릭
        binding.btnAdminAlarmBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun initObserver() {}
}
