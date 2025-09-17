package com.example.assu_fe_app.presentation.partner.signup

import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentPartnerSignUpLicenseBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.user.mypage.UserMypagePrivacyDialogFragment
import com.example.assu_fe_app.presentation.user.signup.UserSignUpTermsDialogFragment
import com.example.assu_fe_app.ui.auth.SignUpViewModel
import com.example.assu_fe_app.util.setProgressBarFillAnimated
import java.io.File

class PartnerSignUpLicenseFragment :
    BaseFragment<FragmentPartnerSignUpLicenseBinding>(R.layout.fragment_partner_sign_up_license) {

    private val signUpViewModel: SignUpViewModel by activityViewModels()
    
    // 갤러리에서 이미지 선택을 위한 launcher
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            // 선택된 이미지 파일을 ViewModel에 저장
            val file = File(selectedUri.path ?: "")
            if (file.exists()) {
                signUpViewModel.setLicenseImageFile(file)
                binding.etPartnerLicense.setText(file.name)
                binding.ivUpload.setImageResource(R.drawable.ic_signup_verified)
                setButtonEnabled(true)
            }
        }
    }

    override fun initObserver() {}

    override fun initView() {
        binding.ivSignupProgressBar.setProgressBarFillAnimated(
            container = binding.flSignupProgressContainer,
            fromPercent = 0.7f,
            toPercent = 0.85f
        )
        // 처음엔 비활성화
        setButtonEnabled(false)

        // 업로드 버튼 클릭
        binding.ivUpload.setOnClickListener {
            // 갤러리에서 이미지 선택
            imagePickerLauncher.launch("image/*")
        }

        // 체크박스 리스너 설정
        setupCheckboxListeners()

        // 약관 링크 클릭 리스너
        binding.tvPrivacyLink.setOnClickListener {
            showPrivacyDialog()
        }

        binding.tvTermsLink.setOnClickListener {
            showTermsDialog()
        }

        binding.btnCompleted.setOnClickListener {
            if (binding.btnCompleted.isEnabled) {
                findNavController().navigate(R.id.action_partner_license_to_complete)
            }
        }
    }

    private fun setButtonEnabled(enabled: Boolean) {
        binding.btnCompleted.isEnabled = enabled
        binding.btnCompleted.background = ContextCompat.getDrawable(
            requireContext(),
            if (enabled) R.drawable.btn_basic_selected else R.drawable.btn_basic_unselected
        )
    }

    private fun setupCheckboxListeners() {
        // 전체 동의 체크박스 리스너 설정
        binding.cbAllAgree.setOnCheckedChangeListener { _, isChecked ->
            setAllAgreement(isChecked)
            updateButtonState()
        }

        // 개별 약관 체크박스 리스너 설정
        binding.cbPrivacyAgree.setOnCheckedChangeListener { _, isChecked ->
            signUpViewModel.setPrivacyAgree(isChecked)
            updateAllAgreeState()
            updateButtonState()
        }

        binding.cbTermsAgree.setOnCheckedChangeListener { _, isChecked ->
            signUpViewModel.setTermsAgree(isChecked)
            updateAllAgreeState()
            updateButtonState()
        }
    }

    private fun setAllAgreement(isChecked: Boolean) {
        // 전체 동의 체크박스의 리스너를 일시적으로 비활성화
        binding.cbAllAgree.setOnCheckedChangeListener(null)
        binding.cbPrivacyAgree.setOnCheckedChangeListener(null)
        binding.cbTermsAgree.setOnCheckedChangeListener(null)

        // 모든 체크박스 상태 설정
        binding.cbAllAgree.isChecked = isChecked
        binding.cbPrivacyAgree.isChecked = isChecked
        binding.cbTermsAgree.isChecked = isChecked

        // ViewModel에 상태 저장
        signUpViewModel.setPrivacyAgree(isChecked)
        signUpViewModel.setTermsAgree(isChecked)

        // 리스너 재설정
        setupCheckboxListeners()
    }

    private fun updateAllAgreeState() {
        val isPrivacyAgreed = binding.cbPrivacyAgree.isChecked
        val isTermsAgreed = binding.cbTermsAgree.isChecked

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

    private fun updateButtonState() {
        val isPrivacyAgreed = binding.cbPrivacyAgree.isChecked
        val isTermsAgreed = binding.cbTermsAgree.isChecked
        
        // 필수 약관(개인정보 처리방침, 서비스 이용약관)만 체크되어야 버튼 활성화
        val isButtonEnabled = isPrivacyAgreed && isTermsAgreed
        
        setButtonEnabled(isButtonEnabled)
    }

    private fun showPrivacyDialog() {
        val dialog = UserMypagePrivacyDialogFragment()
        dialog.show(parentFragmentManager, "PrivacyDialog")
    }

    private fun showTermsDialog() {
        val dialog = UserSignUpTermsDialogFragment()
        dialog.show(parentFragmentManager, "TermsDialog")
    }
}