package com.ssu.assu.presentation.partner.signup

import android.Manifest
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ssu.assu.R
import com.ssu.assu.databinding.FragmentPartnerSignUpLicenseBinding
import com.ssu.assu.presentation.base.BaseFragment
import com.ssu.assu.presentation.user.mypage.UserMypagePrivacyDialogFragment
import com.ssu.assu.presentation.user.signup.UserSignUpTermsDialogFragment
import com.ssu.assu.ui.auth.SignUpViewModel
import com.ssu.assu.util.setProgressBarFillAnimated
import com.google.firebase.analytics.FirebaseAnalytics
import java.io.File

class PartnerSignUpLicenseFragment :
    BaseFragment<FragmentPartnerSignUpLicenseBinding>(R.layout.fragment_partner_sign_up_license) {

    private val signUpViewModel: SignUpViewModel by activityViewModels()
    
    // 갤러리 접근 권한 요청을 위한 launcher (API 레벨에 따라 다름)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            // 권한이 허용된 경우 갤러리 열기
            openGallery()
        } else {
            Toast.makeText(requireContext(), "갤러리 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 갤러리에서 이미지 선택을 위한 launcher
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            processSelectedImage(selectedUri)
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

        // 체크박스 색상 설정
        setupCheckboxColors()

        // 업로드 버튼 클릭
        binding.ivUpload.setOnClickListener {
            // 갤러리 접근 권한 확인 후 갤러리 열기
            checkGalleryPermission()
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
                val analytics = FirebaseAnalytics.getInstance(requireContext())
                analytics.setUserProperty("user_type", "partner")

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

    private fun updateButtonState() {
        val isPrivacyAgreed = binding.cbPrivacyAgree.isChecked
        
        // 필수 약관(개인정보 처리방침 + 서비스 이용약관)이 체크되어야 버튼 활성화
        // 선택 약관(cbMarketingAgree)은 체크 여부와 관계없이 진행 가능
        val isButtonEnabled = isPrivacyAgreed
        
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

    // 갤러리 접근 권한 확인 (API 레벨에 따라 다름)
    private fun checkGalleryPermission() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // API 33 (Android 13) 이상: READ_MEDIA_IMAGES 권한
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            // API 32 이하: READ_EXTERNAL_STORAGE 권한
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        
        val allPermissionsGranted = permissions.all { permission ->
            ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED
        }
        
        if (allPermissionsGranted) {
            // 권한이 이미 허용된 경우 갤러리 열기
            openGallery()
        } else {
            // 권한 요청
            requestPermissionLauncher.launch(permissions)
        }
    }

    // 갤러리 열기
    private fun openGallery() {
        imagePickerLauncher.launch("image/*")
    }

    // 선택된 이미지 처리
    private fun processSelectedImage(uri: Uri) {
        val fileName = getFileNameFromUri(uri)
        try {
            // URI를 File로 변환
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val file = File(requireContext().cacheDir, fileName)
            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            
            if (file.exists()) {
                // ViewModel에 파일 저장
                signUpViewModel.setLicenseImageFile(file)
                
                // UI 업데이트
                val truncatedFileName = truncateFileName(fileName, 20)
                binding.etPartnerLicense.setText(truncatedFileName)
                binding.ivUpload.setImageResource(R.drawable.ic_signup_verified)
                setButtonEnabled(true)
                
                Toast.makeText(requireContext(), "사진이 업로드되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "파일 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "파일 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // URI에서 파일명 가져오기
    private fun getFileNameFromUri(uri: Uri): String {
        val contentResolver: ContentResolver = requireContext().contentResolver
        return contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0 && cursor.moveToFirst()) {
                cursor.getString(nameIndex) ?: "unknown_file"
            } else {
                "unknown_file"
            }
        } ?: "unknown_file"
    }

    // 파일명 길이 제한
    private fun truncateFileName(fileName: String, maxLength: Int): String {
        return if (fileName.length > maxLength) {
            val extension = fileName.substringAfterLast(".", "")
            val nameWithoutExtension = fileName.substringBeforeLast(".")
            val truncatedName = nameWithoutExtension.take(maxLength - extension.length - 4) // 4는 "..." + "." 길이
            "$truncatedName...$extension"
        } else {
            fileName
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