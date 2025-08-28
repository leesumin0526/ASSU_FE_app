package com.example.assu_fe_app.presentation.partner.mypage

import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentPartnerNotificationSettingBinding
import com.example.assu_fe_app.presentation.base.BaseFragment

class PartnerNotificationSettingFragment : BaseFragment<FragmentPartnerNotificationSettingBinding>(R.layout.fragment_partner_notification_setting) {
    override fun initView() {
        binding.btnPartnerNotiSettingBack.setOnClickListener {
            findNavController().popBackStack()
        }

        //이전 설정 상태를 불러오는 과정
        var isActivated = true


        // 상태 반전
        binding.btnPartnerNotiSettingPushToggle.setOnClickListener {
            isActivated = !isActivated

            if (isActivated) {
                binding.btnPartnerNotiSettingPushToggle.setImageResource(R.drawable.ic_toggle_able)
            } else {
                binding.btnPartnerNotiSettingPushToggle.setImageResource(R.drawable.ic_toggle_unable)
            }
        }
    }

    override fun initObserver() {

    }


}