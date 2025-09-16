package com.example.assu_fe_app.presentation.partner.signup

import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentPartnerSignUpLicenseBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.signup.SignUpViewModel
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
}