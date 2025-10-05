package com.assu.app.presentation.admin.mypage

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.assu.app.databinding.FragmentAdminMypageAlarmBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AdminMypageAlarmDialogFragment : DialogFragment() {

    private var _binding: FragmentAdminMypageAlarmBinding? = null
    private val binding get() = _binding!!
    private val vm: AdminAlarmViewModel by viewModels()

    // 프로그램적으로 체크값을 바꿀 때 리스너 재진입 방지
    private var updatingUI = false

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

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        // 혹시 버튼 경로를 안 탔다면 여기서 한 번 더 저장 시도
        lifecycleScope.launch { vm.commit() }
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

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // 저장 후 닫기
                    viewLifecycleOwner.lifecycleScope.launch {
                        vm.commit()
                        dismiss()
                    }
                }
            }
        )


        // 뒤로가기
        binding.btnAdminAlarmBack.setOnClickListener { dismiss() }

        // 컨테이너는 항상 보이게 (GONE 금지)
        binding.clAdminAlarmPartnershipRequest.visibility = View.VISIBLE
        binding.clAdminAlarmPartnershipProposal.visibility = View.VISIBLE
        binding.clAdminAlarmPartnershipChatting.visibility = View.VISIBLE

        // ====== 리스너: VM 이벤트로 위임 ======
        binding.switchAdminAlarmPush.setOnCheckedChangeListener { _, checked ->
            if (updatingUI) return@setOnCheckedChangeListener
            vm.onMasterClick(checked)              // false -> 전부 OFF, true -> 유지
        }
        binding.switchAdminAlarmPartnershipRequest.setOnCheckedChangeListener { _, checked ->
            if (updatingUI) return@setOnCheckedChangeListener
            vm.onSuggestionClick(checked)          // PARTNER_SUGGESTION
        }
        binding.switchAdminAlarmPartnershipProposal.setOnCheckedChangeListener { _, checked ->
            if (updatingUI) return@setOnCheckedChangeListener
            vm.onProposalClick(checked)            // PARTNER_PROPOSAL
        }
        binding.switchAdminAlarmPartnershipChatting.setOnCheckedChangeListener { _, checked ->
            if (updatingUI) return@setOnCheckedChangeListener
            vm.onChatClick(checked)                // CHAT
        }

        // ====== 플로우 수집: prefs & master ======
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 하위 스위치 상태 반영
                launch {
                    vm.prefs.collect { p ->
                        updatingUI = true
                        try {
                            binding.switchAdminAlarmPartnershipRequest.isChecked = p.suggestion
                            binding.switchAdminAlarmPartnershipProposal.isChecked = p.proposal
                            binding.switchAdminAlarmPartnershipChatting.isChecked = p.chat
                        } finally {
                            updatingUI = false
                        }
                    }
                }
                // 마스터 표시(OR)
                launch {
                    vm.master.collect { m ->
                        updatingUI = true
                        try {
                            binding.switchAdminAlarmPush.isChecked = m
                        } finally {
                            updatingUI = false
                        }
                    }
                }
            }
        }

        // ====== 진입 시 1회 서버 로드 트리거 ======
        vm.load()

        // 닫기 버튼: 커밋 후 닫기
        binding.btnAdminAlarmBack.setOnClickListener {
            // Dialog를 닫기 전에 저장을 끝낸다
            lifecycleScope.launch {
                vm.commit()
                dismiss()   // 저장 끝난 다음 닫기
            }
        }

    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}