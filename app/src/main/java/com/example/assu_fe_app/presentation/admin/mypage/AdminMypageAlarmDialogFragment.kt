package com.example.assu_fe_app.presentation.admin.mypage

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentAdminMypageAlarmBinding

class AdminMypageAlarmDialogFragment : DialogFragment() {

    private var _binding: FragmentAdminMypageAlarmBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminMypageAlarmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 뒤로가기 버튼 클릭
        binding.btnAdminAlarmBack.setOnClickListener {
            dismiss()
        }

        var isActivated = true
        var isRequestActivated = false
        var isProposalActivated = false

        // 전체 알림 토글
        binding.switchAdminAlarmPush.setOnClickListener {
            isActivated = !isActivated

            if (isActivated) {
                binding.clAdminAlarmPartnershipProposal.visibility = View.VISIBLE
                binding.clAdminAlarmPartnershipRequest.visibility = View.VISIBLE
            } else {
                binding.clAdminAlarmPartnershipProposal.visibility = View.GONE
                binding.clAdminAlarmPartnershipRequest.visibility = View.GONE
            }
        }

        // 제안 알림 토글
        binding.switchAdminAlarmPartnershipRequest.setOnClickListener {
            isRequestActivated = !isRequestActivated

        }

        // 제휴 알림 토글
        binding.switchAdminAlarmPartnershipProposal.setOnClickListener {
            isProposalActivated = !isProposalActivated

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
