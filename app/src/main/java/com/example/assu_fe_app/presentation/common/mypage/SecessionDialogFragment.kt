package com.example.assu_fe_app.presentation.common.mypage

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.assu_fe_app.databinding.DialogSecessionBinding
import com.example.assu_fe_app.presentation.common.login.LoginActivity
import com.example.assu_fe_app.ui.auth.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SecessionDialogFragment : DialogFragment() {

    private var _binding: DialogSecessionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MypageViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()

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
        _binding = DialogSecessionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // WithdrawState 관찰
        loginViewModel.withdrawState.observe(viewLifecycleOwner, Observer { state ->
            Log.d("SecessionDialog", "WithdrawState changed: $state")
            when (state) {
                is LoginViewModel.WithdrawState.Loading -> {
                    Log.d("SecessionDialog", "Loading state")
                    // 로딩 중일 때는 버튼 비활성화
                    binding.btnConfirm.isEnabled = false
                    binding.btnCancel.isEnabled = false
                    binding.btnConfirm.text = "처리중..."
                }
                is LoginViewModel.WithdrawState.Success -> {
                    Log.d("SecessionDialog", "Success state - navigating to LoginActivity")
                    // 회원탈퇴 성공 시 LoginActivity로 이동
                    val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                    dismiss()
                }
                is LoginViewModel.WithdrawState.Error -> {
                    Log.d("SecessionDialog", "Error state: ${state.message}")
                    // 에러 발생 시 버튼 다시 활성화하고 다이얼로그 닫기
                    binding.btnConfirm.isEnabled = true
                    binding.btnCancel.isEnabled = true
                    binding.btnConfirm.text = "예"
                    dismiss()
                }
                else -> {
                    Log.d("SecessionDialog", "Other state: $state")
                }
            }
        })

        // 바깥 영역 클릭 닫기
        binding.backgroundOverlay.setOnClickListener { dismiss() }

        // 취소
        binding.btnCancel.setOnClickListener { dismiss() }

        // 확인 (회원 탈퇴 api 연결 지점)
        binding.btnConfirm.setOnClickListener {
            Log.d("SecessionDialog", "Confirm button clicked")
            viewModel.logoutAndUnregister()
            loginViewModel.withdraw()
            // WithdrawState 관찰자에서 성공 시 이동 처리
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