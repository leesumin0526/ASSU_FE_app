package com.example.assu_fe_app.presentation.common.signup

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentSignUpAccountBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.ui.auth.SignUpViewModel
import com.example.assu_fe_app.util.setProgressBarFillAnimated
import kotlinx.coroutines.launch

class SignUpAccountFragment : BaseFragment<FragmentSignUpAccountBinding>(R.layout.fragment_sign_up_account) {
    
    private val signUpViewModel: SignUpViewModel by activityViewModels()
    private var selectedUserType: String? = null
    private var isEmailVerified = false
    private var emailVerificationHandler: Handler? = null
    private val EMAIL_VERIFICATION_DELAY = 1000L // 1초 지연
    
    override fun initObserver() {
        // 이메일 검증 상태 관찰
        lifecycleScope.launch {
            signUpViewModel.isEmailVerifying.collect { isVerifying ->
                // 로딩 상태 처리 (필요시 UI 업데이트)
            }
        }
        
        lifecycleScope.launch {
            signUpViewModel.emailVerificationResult.collect { result ->
                isEmailVerified = result == true
                checkInputValidity()
            }
        }
        
        lifecycleScope.launch {
            signUpViewModel.emailVerificationMessage.collect { message ->
                message?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    
                    // 409 에러 시 이메일 입력 필드 빨간색 표시
                    if (it.contains("이미 가입된 이메일") || it.contains("이미 사용 중인 이메일")) {
                        showEmailError()
                    }
                }
            }
        }
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

        // 텍스트 변경 감지 리스너 등록 (디바운싱 적용)
        binding.etUserId.addTextChangedListener { 
            isEmailVerified = false // 이메일이 변경되면 검증 상태 초기화
            signUpViewModel.resetEmailVerification() // ViewModel 상태도 초기화
            resetEmailUI() // UI 상태 초기화
            checkInputValidity()
            
            // 기존 핸들러 제거
            emailVerificationHandler?.removeCallbacksAndMessages(null)
            
            // 새로운 핸들러로 지연 검증
            emailVerificationHandler = Handler(Looper.getMainLooper())
            emailVerificationHandler?.postDelayed({
                val email = binding.etUserId.text.toString().trim()
                if (email.isNotEmpty()) {
                    signUpViewModel.checkEmailVerification(email)
                }
            }, EMAIL_VERIFICATION_DELAY)
        }
        binding.etUserPw.addTextChangedListener { checkInputValidity() }
        
        // 이메일 포커스 잃을 때 즉시 검증 (디바운싱 우회)
        binding.etUserId.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                // 핸들러 제거하고 즉시 검증
                emailVerificationHandler?.removeCallbacksAndMessages(null)
                val email = binding.etUserId.text.toString().trim()
                if (email.isNotEmpty()) {
                    signUpViewModel.checkEmailVerification(email)
                }
            }
        }
        
        // 이메일 중복 검증 버튼 클릭 리스너 (필요시 추가)
        // binding.btnEmailCheck.setOnClickListener {
        //     val email = binding.etUserId.text.toString().trim()
        //     if (email.isNotEmpty()) {
        //         signUpViewModel.checkEmailVerification(email)
        //     }
        // }

        binding.btnCompleted.setOnClickListener {
            if (binding.btnCompleted.isEnabled) {
                // ViewModel에 이메일과 비밀번호 저장
                val email = binding.etUserId.text.toString().trim()
                val password = binding.etUserPw.text.toString().trim()
                
                // 이메일 중복 검증이 완료되지 않은 경우
                if (!isEmailVerified) {
                    Toast.makeText(requireContext(), "이메일 중복 검증을 완료해주세요.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                
                // 비밀번호 8자리 이상 검증
                if (password.length < 8) {
                    Toast.makeText(requireContext(), "비밀번호는 8자리 이상이어야 합니다.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                
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

        val isValid = !userId.isNullOrEmpty() && !userPw.isNullOrEmpty() && isEmailVerified
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

    private fun showEmailError() {
        // 이메일 입력 필드 빨간색 표시
        binding.etUserId.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_signup_input_bar_error)
    }

    private fun resetEmailUI() {
        // 이메일 입력 필드 상태 초기화
        binding.etUserId.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_signup_input_bar)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 핸들러 정리
        emailVerificationHandler?.removeCallbacksAndMessages(null)
        emailVerificationHandler = null
    }
}