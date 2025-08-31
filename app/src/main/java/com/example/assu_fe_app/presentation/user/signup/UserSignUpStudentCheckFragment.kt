package com.example.assu_fe_app.presentation.user.signup

import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentUserSignUpStudentCheckBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.util.setProgressBarFillAnimated

class UserSignUpStudentCheckFragment : 
    BaseFragment<FragmentUserSignUpStudentCheckBinding>(R.layout.fragment_user_sign_up_student_check) {

    override fun initObserver() {}

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

        // 완료 버튼 클릭
        binding.btnCompleted.setOnClickListener {
            findNavController().navigate(R.id.action_user_student_check_to_complete)
        }
    }
}
