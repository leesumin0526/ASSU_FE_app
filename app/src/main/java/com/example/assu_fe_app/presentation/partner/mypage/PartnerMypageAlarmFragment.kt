package com.example.assu_fe_app.presentation.partner.mypage

import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentPartnerMypageAlarmBinding
import com.example.assu_fe_app.presentation.base.BaseFragment

class PartnerMypageAlarmFragment : BaseFragment<FragmentPartnerMypageAlarmBinding>(R.layout.fragment_partner_mypage_alarm) {

    override fun initView() {
        // 뒤로가기 버튼 클릭
        binding.btnAlarmBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun initObserver() {}
}
