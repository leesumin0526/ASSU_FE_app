package com.assu.app.presentation.partner.mypage

import androidx.navigation.fragment.findNavController
import com.assu.app.R
import com.assu.app.databinding.FragmentPartnerMypageAlarmBinding
import com.assu.app.presentation.base.BaseFragment

class PartnerMypageAlarmFragment : BaseFragment<FragmentPartnerMypageAlarmBinding>(R.layout.fragment_partner_mypage_alarm) {

    override fun initView() {
        // 뒤로가기 버튼 클릭
        binding.btnAlarmBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun initObserver() {}
}
