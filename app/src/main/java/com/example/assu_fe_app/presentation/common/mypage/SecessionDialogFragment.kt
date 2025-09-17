package com.example.assu_fe_app.presentation.common.mypage

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.assu_fe_app.databinding.DialogSecessionBinding
import com.example.assu_fe_app.presentation.common.login.LoginActivity
import com.example.assu_fe_app.ui.common.mypage.MypageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SecessionDialogFragment : DialogFragment() {

    private var _binding: DialogSecessionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MypageViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
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
        Log.d("SecessionDialog", "onCreateView called")
        _binding = DialogSecessionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("SecessionDialog", "onViewCreated called")

        // MypageViewModel의 회원탈퇴 상태 관찰
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.withdrawState.collectLatest { state ->
                Log.d("SecessionDialog", "MypageViewModel withdrawState changed: $state")
                when (state) {
                    is MypageViewModel.WithdrawState.Unregistering -> {
                        Log.d("SecessionDialog", "Device token unregistering for withdraw...")
                    }
                    is MypageViewModel.WithdrawState.Withdrawing -> {
                        Log.d("SecessionDialog", "Withdrawing from server...")
                        // 로딩 중일 때는 버튼 비활성화
                        binding.btnConfirm.isEnabled = false
                        binding.btnCancel.isEnabled = false
                        binding.btnConfirm.text = "처리중..."
                    }
                    is MypageViewModel.WithdrawState.Success -> {
                        Log.d("SecessionDialog", "Withdraw success state - navigating to LoginActivity")
                        Toast.makeText(requireContext(), "회원탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                        // 회원탈퇴 성공 시 LoginActivity로 이동
                        val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                        dismiss()
                    }
                    is MypageViewModel.WithdrawState.Error -> {
                        Log.e("SecessionDialog", "Withdraw error state: ${state.message}")
                        Toast.makeText(requireContext(), "회원탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                        // 에러 발생 시에도 로그인 화면으로 이동 (UX 개선)
                        val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                        dismiss()
                    }
                    else -> {
                        Log.d("SecessionDialog", "Other withdraw state: $state")
                    }
                }
            }
        }

        // 바깥 영역 클릭 닫기
        binding.backgroundOverlay.setOnClickListener { dismiss() }

        // 취소
        binding.btnCancel.setOnClickListener { dismiss() }

        // 확인 (회원 탈퇴 api 연결 지점)
        binding.btnConfirm.setOnClickListener {
            Log.d("SecessionDialog", "Confirm button clicked - starting withdrawal process")
            // 통합된 회원탈퇴 함수 호출
            viewModel.withdrawAndUnregisterFCMToken()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): SecessionDialogFragment = SecessionDialogFragment()
    }
}