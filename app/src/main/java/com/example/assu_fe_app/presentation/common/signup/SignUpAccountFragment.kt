package com.example.assu_fe_app.presentation.common.signup

import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentSignUpAccountBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.util.setProgressBarFillAnimated

class SignUpAccountFragment : BaseFragment<FragmentSignUpAccountBinding>(R.layout.fragment_sign_up_account) {
    override fun initObserver() {
    }

    override fun initView() {
        binding.ivSignupProgressBar.setProgressBarFillAnimated(
            container = binding.flSignupProgressContainer,
            fromPercent = 0.2f,
            toPercent = 0.35f
        )

        setButtonEnabled(false) // 초기에는 비활성화

        // 텍스트 변경 감지 리스너 등록
        binding.etUserId.addTextChangedListener { checkInputValidity() }
        binding.etUserPw.addTextChangedListener { checkInputValidity() }

        binding.btnCompleted.setOnClickListener {
            if (binding.btnCompleted.isEnabled) {
                findNavController().navigate(R.id.action_account_to_type)
            }
        }
    }

    // 입력값 유효성 확인 함수
    private fun checkInputValidity() {
        val userId = binding.etUserId.text?.toString()?.trim()
        val userPw = binding.etUserPw.text?.toString()?.trim()

        val isValid = !userId.isNullOrEmpty() && !userPw.isNullOrEmpty()
        setButtonEnabled(isValid)
    }

    // 버튼 상태 토글 함수
    private fun setButtonEnabled(enabled: Boolean) {
        binding.btnCompleted.isEnabled = enabled
        binding.btnCompleted.background = ContextCompat.getDrawable(
            requireContext(),
            if (enabled) R.drawable.btn_basic_selected else R.drawable.btn_basic_unselected
        )
    }
}