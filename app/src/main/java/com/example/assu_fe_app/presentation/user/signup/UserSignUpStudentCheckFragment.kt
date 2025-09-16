package com.example.assu_fe_app.presentation.user.signup

import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentUserSignUpStudentCheckBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.signup.SignUpViewModel
import com.example.assu_fe_app.util.setProgressBarFillAnimated

class UserSignUpStudentCheckFragment : 
    BaseFragment<FragmentUserSignUpStudentCheckBinding>(R.layout.fragment_user_sign_up_student_check) {

    private val signUpViewModel: SignUpViewModel by activityViewModels()

    override fun initObserver() {
        // 학생 검증 결과 관찰하여 화면에 표시
        signUpViewModel.studentVerifyResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                // 학생 정보를 화면에 표시
                binding.tvStudentName.text = it.name
                binding.tvStudentNumber.text = it.studentNumber
                binding.tvStudentMajor.text = it.major
                binding.tvStudentStatus.text = it.enrollmentStatus
            }
        }
    }

    override fun initView() {
        binding.ivSignupProgressBar.setProgressBarFillAnimated(
            container = binding.flSignupProgressContainer,
            fromPercent = 0.70f,
            toPercent = 0.85f
        )

        // "LMS 인증" 부분을 assu_main 컬러로 설정
        binding.tvSchoolAccountCheckTitle.text = buildSpannedString {
            color(ContextCompat.getColor(requireContext(), R.color.assu_main)) {
                append("LMS 인증")
            }
            append("에 성공했어요!\n학부와 학번을 확인해주세요!")
        }

        // 동의 체크박스 리스너 설정
        binding.cbMarketingAgree.setOnCheckedChangeListener { _, isChecked ->
            signUpViewModel.setMarketingAgree(isChecked)
            updateButtonState()
        }
        
        binding.cbLocationAgree.setOnCheckedChangeListener { _, isChecked ->
            signUpViewModel.setLocationAgree(isChecked)
            updateButtonState()
        }

        // 완료 버튼 클릭
        binding.btnCompleted.setOnClickListener {
            findNavController().navigate(R.id.action_user_student_check_to_complete)
        }
        
        // 초기 버튼 상태 설정
        updateButtonState()
    }
    
    private fun updateButtonState() {
        val isMarketingAgreed = binding.cbMarketingAgree.isChecked
        val isLocationAgreed = binding.cbLocationAgree.isChecked
        
        // 두 동의 항목 모두 체크되어야 버튼 활성화
        val isButtonEnabled = isMarketingAgreed && isLocationAgreed
        
        binding.btnCompleted.isEnabled = isButtonEnabled
        binding.btnCompleted.background = ContextCompat.getDrawable(
            requireContext(),
            if (isButtonEnabled) R.drawable.btn_basic_selected else R.drawable.btn_basic_unselected
        )
    }
}
