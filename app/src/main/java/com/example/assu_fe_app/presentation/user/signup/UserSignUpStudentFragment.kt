package com.example.assu_fe_app.presentation.user.signup

import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentUserSignUpStudentBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.util.setProgressBarFillAnimated

class UserSignUpStudentFragment :
    BaseFragment<FragmentUserSignUpStudentBinding>(R.layout.fragment_user_sign_up_student) {

    override fun initObserver() {}

    override fun initView() {
        binding.ivSignupProgressBar.setProgressBarFillAnimated(
            container = binding.flSignupProgressContainer,
            fromPercent = 0.75f,
            toPercent = 0.90f
        )

        val schoolName = "숭실대학교" // 이후 동적으로 변경 가능
        val baseText = getString(R.string.school_account_text, schoolName)

        binding.tvSchoolAccountTitle.text = buildSpannedString {
            append(" ")
            color(ContextCompat.getColor(requireContext(), R.color.assu_font_main)) {
                append(schoolName)
            }
            append(" 학생이시군요!\n학번과 비밀번호를\n입력해주세요!")
        }

        // 텍스트 입력 감지 리스너 등록
        binding.etSchoolId.addTextChangedListener { checkInputValidity() }
        binding.etShoolPw.addTextChangedListener { checkInputValidity() }

        // 완료 버튼 클릭
        binding.btnCompleted.setOnClickListener {
            val id = binding.etSchoolId.text.toString()
            val pw = binding.etShoolPw.text.toString()

            if (validateStudentAccount(id, pw)) {
                findNavController().navigate(R.id.action_user_student_to_complete)
            } else {
                Toast.makeText(requireContext(), "학번 또는 비밀번호가 올바르지 않습니다", Toast.LENGTH_SHORT).show()
            }
        }

        // 초기 상태는 버튼 비활성화
        setButtonEnabled(false)
    }

    private fun checkInputValidity() {
        val id = binding.etSchoolId.text.toString().trim()
        val pw = binding.etShoolPw.text.toString().trim()
        setButtonEnabled(id.isNotEmpty() && pw.isNotEmpty())
    }

    private fun setButtonEnabled(enabled: Boolean) {
        binding.btnCompleted.isEnabled = enabled
        binding.btnCompleted.background = ContextCompat.getDrawable(
            requireContext(),
            if (enabled) R.drawable.btn_basic_selected else R.drawable.btn_basic_unselected
        )
    }

    // 유세인트 인증 로직 추가 예정
    private fun validateStudentAccount(id: String, pw: String): Boolean {
        // 임시 로직: "20211234" "1234"일 경우만 통과
        return id == "20211234" && pw == "1234"
    }
}
