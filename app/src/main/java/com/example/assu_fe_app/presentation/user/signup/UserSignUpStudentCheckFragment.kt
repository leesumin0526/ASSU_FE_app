package com.example.assu_fe_app.presentation.user.signup

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentUserSignUpStudentCheckBinding
import com.example.assu_fe_app.domain.model.enums.Major
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.user.mypage.UserMypagePrivacyDialogFragment
import com.example.assu_fe_app.ui.auth.SignUpViewModel
import com.example.assu_fe_app.util.setProgressBarFillAnimated
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.launch

class UserSignUpStudentCheckFragment : 
    BaseFragment<FragmentUserSignUpStudentCheckBinding>(R.layout.fragment_user_sign_up_student_check) {

    private val signUpViewModel: SignUpViewModel by activityViewModels()

    override fun initObserver() {
        // 학생 검증 결과 관찰하여 화면에 표시
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                signUpViewModel.studentVerifyResult.collect { result ->
                    result?.let {
                        // Major enum을 한국어로 변환하여 표시
                        val majorDisplayName = convertMajorToKorean(it.major)
                        binding.etStudentMajor.setText(majorDisplayName)
                        binding.etStudentId.setText(it.studentNumber)
                    }
                }
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

        // 체크박스 색상 설정
        setupCheckboxColors()

        // 체크박스 리스너 설정
        setupCheckboxListeners()

        // 개인정보 처리방침 링크 클릭
        binding.tvPrivacyLink.setOnClickListener {
            showPrivacyDialog()
        }

        // 서비스 이용약관 링크 클릭
        binding.tvTermsLink.setOnClickListener {
            showTermsDialog()
        }

        // 완료 버튼 클릭
        binding.btnCompleted.setOnClickListener {
            val analytics = FirebaseAnalytics.getInstance(requireContext())
            analytics.setUserProperty("user_type", "student")

            findNavController().navigate(R.id.action_user_student_check_to_complete)
        }
        
        // 초기 버튼 상태 설정
        updateButtonState()
    }
    
    private fun updateButtonState() {
        val isPrivacyAgreed = binding.cbPrivacyAgree.isChecked

        // 필수 약관(개인정보 처리방침 + 서비스 이용약관)이 체크되어야 버튼 활성화
        // 선택 약관(cbMarketingAgree)은 체크 여부와 관계없이 진행 가능
        val isButtonEnabled = isPrivacyAgreed
        
        binding.btnCompleted.isEnabled = isButtonEnabled
        binding.btnCompleted.background = ContextCompat.getDrawable(
            requireContext(),
            if (isButtonEnabled) R.drawable.btn_basic_selected else R.drawable.btn_basic_unselected
        )
    }

    private fun showPrivacyDialog() {
        val dialog = UserMypagePrivacyDialogFragment()
        dialog.show(parentFragmentManager, "PrivacyDialog")
    }

    private fun showTermsDialog() {
        val dialog = UserSignUpTermsDialogFragment()
        dialog.show(parentFragmentManager, "TermsDialog")
    }

    private fun setAllAgreement(isChecked: Boolean) {
        // 전체 동의 체크박스의 리스너를 일시적으로 비활성화
        binding.cbAllAgree.setOnCheckedChangeListener(null)
        binding.cbPrivacyAgree.setOnCheckedChangeListener(null)
        binding.cbMarketingAgree.setOnCheckedChangeListener(null)

        // 모든 체크박스 상태 설정
        binding.cbAllAgree.isChecked = isChecked
        binding.cbPrivacyAgree.isChecked = isChecked
        binding.cbMarketingAgree.isChecked = isChecked

        // ViewModel에 상태 저장
        signUpViewModel.setLocationAgree(isChecked) // 필수 약관 (개인정보처리방침 + 위치정보 수집동의)
        signUpViewModel.setMarketingAgree(isChecked) // 선택 약관 (Email 및 SMS 마케팅 수신 동의)

        // 리스너 재설정
        setupCheckboxListeners()
    }

    private fun updateAllAgreeState() {
        val isPrivacyAgreed = binding.cbPrivacyAgree.isChecked
        val isTermsAgreed = binding.cbMarketingAgree.isChecked

        // 모든 개별 약관이 체크되어 있으면 전체 동의도 체크
        val allChecked = isPrivacyAgreed && isTermsAgreed
        
        // 전체 동의 체크박스의 리스너를 일시적으로 비활성화
        binding.cbAllAgree.setOnCheckedChangeListener(null)
        binding.cbAllAgree.isChecked = allChecked
        // 리스너 재설정
        binding.cbAllAgree.setOnCheckedChangeListener { _, isChecked ->
            setAllAgreement(isChecked)
            updateButtonState()
        }
    }

    private fun setupCheckboxListeners() {
        // 전체 동의 체크박스 리스너 설정
        binding.cbAllAgree.setOnCheckedChangeListener { _, isChecked ->
            setAllAgreement(isChecked)
            updateButtonState()
        }

        // 개별 약관 체크박스 리스너 설정
        binding.cbPrivacyAgree.setOnCheckedChangeListener { _, isChecked ->
            signUpViewModel.setLocationAgree(isChecked) // 필수 약관 (개인정보처리방침 + 위치정보 수집동의)
            updateAllAgreeState()
            updateButtonState()
        }

        binding.cbMarketingAgree.setOnCheckedChangeListener { _, isChecked ->
            signUpViewModel.setMarketingAgree(isChecked) // 선택 약관 (Email 및 SMS 마케팅 수신 동의)
            updateAllAgreeState()
            updateButtonState()
        }
    }

    // Major enum을 한국어로 변환하는 함수
    private fun convertMajorToKorean(majorString: String?): String {
        if (majorString.isNullOrEmpty()) {
            return "학과 정보 없음"
        }
        
        return try {
            // Major enum에서 해당하는 항목을 찾아서 displayName 반환
            val majorEnum = Major.values().find { it.name == majorString }
            majorEnum?.displayName ?: majorString
        } catch (e: Exception) {
            // 변환 실패 시 원본 문자열 반환
            majorString
        }
    }

    // 체크박스 색상 설정
    private fun setupCheckboxColors() {
        val assuMainColor = ContextCompat.getColor(requireContext(), R.color.assu_main)
        val assuFontSubColor = ContextCompat.getColor(requireContext(), R.color.assu_font_sub)
        
        // 체크된 상태와 체크되지 않은 상태의 색상 설정
        val colorStateList = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf(-android.R.attr.state_checked)
            ),
            intArrayOf(assuMainColor, assuFontSubColor)
        )
        
        // 모든 체크박스에 색상 적용
        binding.cbAllAgree.buttonTintList = colorStateList
        binding.cbPrivacyAgree.buttonTintList = colorStateList
        binding.cbMarketingAgree.buttonTintList = colorStateList
    }
}
