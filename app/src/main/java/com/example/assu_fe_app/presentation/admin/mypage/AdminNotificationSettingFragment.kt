package com.example.assu_fe_app.presentation.admin.mypage

import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentAdminNotificationSettingBinding
import com.example.assu_fe_app.presentation.base.BaseFragment

class AdminNotificationSettingFragment :
    BaseFragment<FragmentAdminNotificationSettingBinding>(R.layout.fragment_admin_notification_setting) {

    override fun initObserver() {}

    override fun initView() {
        binding.btnAdminNotiSettingBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // 초기 상태
        var isActivated = true
        var isProposalActivated = false
        var isSuggestionActivated = false

        // 전체 알림 토글
        binding.btnAdminNotiSettingPushToggle.setOnClickListener {
            isActivated = !isActivated

            if (isActivated) {
                binding.btnAdminNotiSettingPushToggle.setImageResource(R.drawable.ic_toggle_able)

                binding.btnAdminNotiSettingPartnershipSuggestionToggle.visibility = View.VISIBLE
                binding.btnAdminNotiSettingPartnershipProposalToggle.visibility = View.VISIBLE
                binding.tvAdminNotiProposal.visibility = View.VISIBLE
                binding.tvAdminNotiSuggestion.visibility = View.VISIBLE
            } else {
                binding.btnAdminNotiSettingPushToggle.setImageResource(R.drawable.ic_toggle_unable)

                binding.btnAdminNotiSettingPartnershipSuggestionToggle.visibility = View.GONE
                binding.btnAdminNotiSettingPartnershipProposalToggle.visibility = View.GONE
                binding.tvAdminNotiProposal.visibility = View.GONE
                binding.tvAdminNotiSuggestion.visibility = View.GONE
            }
        }

        // 제안 알림 토글
        binding.btnAdminNotiSettingPartnershipSuggestionToggle.setOnClickListener {
            isSuggestionActivated = !isSuggestionActivated
            binding.btnAdminNotiSettingPartnershipSuggestionToggle.setImageResource(
                if (isSuggestionActivated) R.drawable.ic_toggle_able else R.drawable.ic_toggle_unable
            )
        }

        // 제휴 알림 토글
        binding.btnAdminNotiSettingPartnershipProposalToggle.setOnClickListener {
            isProposalActivated = !isProposalActivated
            binding.btnAdminNotiSettingPartnershipProposalToggle.setImageResource(
                if (isProposalActivated) R.drawable.ic_toggle_able else R.drawable.ic_toggle_unable
            )
        }
    }
}