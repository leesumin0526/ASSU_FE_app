package com.example.assu_fe_app.presentation.partner.mypage

import android.content.Intent
import androidx.navigation.Navigation
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentPartnerMypageBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.login.LoginActivity

class PartnerMypageFragment : BaseFragment<FragmentPartnerMypageBinding>(R.layout.fragment_partner_mypage) {

    override fun initView(){

        // 알림 설정 창

        binding.clPartnerAccountComponent1.setOnClickListener { view ->
            Navigation.findNavController(view).navigate(R.id.action_partnerMyPageFragment_to_partnerNotificationSettingFragment)
            val alarmDialogFragment = PartnerMypageAlarmDialogFragment()
            alarmDialogFragment.show(childFragmentManager, "AlarmDialog")

        // 로그아웃 창
        binding.clPartnerAccountComponent2.setOnClickListener {
            val intent = Intent( requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
    }

    override fun initObserver(){}

}