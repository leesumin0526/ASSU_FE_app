package com.ssu.assu.presentation.partner.mypage

import androidx.navigation.fragment.findNavController
import com.ssu.assu.R
import com.ssu.assu.databinding.FragmentPartnerMypageAlarmBinding
import com.ssu.assu.presentation.base.BaseFragment

class PartnerMypageAlarmFragment : BaseFragment<FragmentPartnerMypageAlarmBinding>(R.layout.fragment_partner_mypage_alarm) {

    override fun initView() {
        // 뒤로가기 버튼 클릭
        binding.btnAlarmBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun initObserver() {}
}
