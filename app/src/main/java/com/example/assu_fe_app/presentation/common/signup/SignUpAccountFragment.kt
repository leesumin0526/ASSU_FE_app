package com.example.assu_fe_app.presentation.common.signup

import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentSignUpAccountBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.signup.SignUpViewModel
import com.example.assu_fe_app.util.setProgressBarFillAnimated

class SignUpAccountFragment : BaseFragment<FragmentSignUpAccountBinding>(R.layout.fragment_sign_up_account) {
    
    private val signUpViewModel: SignUpViewModel by activityViewModels()
    private var selectedUserType: String? = null
    
    override fun initObserver() {
    }

    override fun initView() {
        // 이전 프래그먼트에서 전달받은 사용자 타입 확인
        selectedUserType = arguments?.getString("userType")
        
        binding.ivSignupProgressBar.setProgressBarFillAnimated(
            container = binding.flSignupProgressContainer,
            fromPercent = 0.4f,
            toPercent = 0.55f
        )

        setButtonEnabled(false) // 초기에는 비활성화

        // 텍스트 변경 감지 리스너 등록
        binding.etUserId.addTextChangedListener { checkInputValidity() }
        binding.etUserPw.addTextChangedListener { checkInputValidity() }

        binding.btnCompleted.setOnClickListener {
            if (binding.btnCompleted.isEnabled) {
                // ViewModel에 이메일과 비밀번호 저장
                val email = binding.etUserId.text.toString().trim()
                val password = binding.etUserPw.text.toString().trim()
                signUpViewModel.setEmail(email)
                signUpViewModel.setPassword(password)
                
                when (selectedUserType) {
                    "admin" -> {
                        findNavController().navigate(R.id.action_account_to_admin_info)
                    }
                    "partner" -> {
                        findNavController().navigate(R.id.action_account_to_partner_info)
                    }
                    else -> {
                        // 기본값으로 partner로 이동
                        findNavController().navigate(R.id.action_account_to_partner_info)
                    }
                }
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