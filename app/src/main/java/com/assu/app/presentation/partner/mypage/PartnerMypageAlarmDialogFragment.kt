package com.assu.app.presentation.partner.mypage

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.view.KeyEvent
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.assu.app.databinding.FragmentPartnerMypageAlarmBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PartnerMypageAlarmDialogFragment : DialogFragment() {

    private var _binding: FragmentPartnerMypageAlarmBinding? = null
    private val binding get() = _binding!!
    private val vm: PartnerAlarmViewModel by viewModels()
    private var updatingUI = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        super.onCreateDialog(savedInstanceState).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
        }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawableResource(android.R.color.transparent)
        }
        // BACK 키 안전망
        dialog?.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                lifecycleScope.launch {
                    vm.commit()
                    dismiss()
                }
                true
            } else false
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        lifecycleScope.launch { vm.commit() } // 예외 경로 세이프가드(변경 없으면 네트워크 호출 안 함)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, s: Bundle?): View {
        _binding = FragmentPartnerMypageAlarmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)

        // 시스템 뒤로가기(제스처/버튼)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewLifecycleOwner.lifecycleScope.launch {
                        vm.commit()
                        dismiss()
                    }
                }
            }
        )

        // 닫기 버튼: 저장 후 닫기
        binding.btnAlarmBack.setOnClickListener {
            lifecycleScope.launch {
                vm.commit()
                dismiss()
            }
        }

        // 컨테이너는 항상 보이게
        binding.clPartnerAlarmPartnershipChatting.visibility = View.VISIBLE
        binding.clPartnerAlarmPartnershipOrder.visibility = View.VISIBLE

        // 리스너 → VM 위임 (로컬만 변경)
        binding.switchPartnerAlarmPush.setOnCheckedChangeListener { _, checked ->
            if (updatingUI) return@setOnCheckedChangeListener
            vm.onMasterClick(checked)
        }
        binding.switchPartnerAlarmPartnershipChatting.setOnCheckedChangeListener { _, checked ->
            if (updatingUI) return@setOnCheckedChangeListener
            vm.onChatClick(checked)
        }
        binding.switchPartnerAlarmPartnershipOrder.setOnCheckedChangeListener { _, checked ->
            if (updatingUI) return@setOnCheckedChangeListener
            vm.onOrderClick(checked)
        }

        // 플로우 수집
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    vm.prefs.collect { p ->
                        updatingUI = true
                        try {
                            binding.switchPartnerAlarmPartnershipChatting.isChecked = p.chat
                            binding.switchPartnerAlarmPartnershipOrder.isChecked     = p.order
                        } finally { updatingUI = false }
                    }
                }
                launch {
                    vm.master.collect { m ->
                        updatingUI = true
                        try {
                            binding.switchPartnerAlarmPush.isChecked = m
                        } finally { updatingUI = false }
                    }
                }
            }
        }

        // 최초 1회 로드
        vm.load()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}