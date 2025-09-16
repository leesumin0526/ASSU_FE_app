package com.example.assu_fe_app.presentation.partner.signup

import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentPartnerSignUpInfoBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.signup.SignUpViewModel
import com.example.assu_fe_app.util.setProgressBarFillAnimated

class PartnerSignUpInfoFragment :
    BaseFragment<FragmentPartnerSignUpInfoBinding>(R.layout.fragment_partner_sign_up_info) {

    private val signUpViewModel: SignUpViewModel by activityViewModels()
    private var isAddressSearchClicked = false

    override fun initObserver() {}

    override fun initView() {

        parentFragmentManager.setFragmentResultListener("result", this) { _, bundle ->
            val resultData = bundle.getString("selectedAddress")
            Log.d("SignupInfoFragment", "받은 데이터: $resultData")

            binding.etPartnerAddress.text = resultData
        }

        binding.ivSignupProgressBar.setProgressBarFillAnimated(
            container = binding.flSignupProgressContainer,
            fromPercent = 0.55f,
            toPercent = 0.7f
        )
        // 상세주소 입력 비활성화 초기화
        binding.etPartnerAddressDetail.isEnabled = false

        // 주소 돋보기 클릭 시 주소 입력 + 상세주소 활성화
        binding.btnPartnerAddressSearch.setOnClickListener {
            findNavController().navigate(R.id.action_partner_info_to_location)

            binding.etPartnerAddressDetail.isEnabled = true
            isAddressSearchClicked = true
            checkAllInputs()
        }

        // 텍스트 변경 감지 리스너 등록
        binding.etPartnerName.addTextChangedListener { checkAllInputs() }
        binding.etPartnerBusinessNumber.addTextChangedListener { checkAllInputs() }
        binding.etPartnerRepresentative.addTextChangedListener { checkAllInputs() }
        binding.etPartnerAddressDetail.addTextChangedListener { checkAllInputs() }

        // 버튼 클릭 시 다음 화면으로 이동
        binding.btnCompleted.setOnClickListener {
            if (binding.btnCompleted.isEnabled) {
                // ViewModel에 제휴업체 정보 저장
                savePartnerInfoToViewModel()
                findNavController().navigate(R.id.action_partner_info_to_license)
            }
        }

        // 초기 상태는 비활성화
        setButtonEnabled(false)
    }

    private fun checkAllInputs() {
        val isNameFilled = binding.etPartnerName.text?.isNotBlank() == true
        val isBusinessNumberFilled = binding.etPartnerBusinessNumber.text?.isNotBlank() == true
        val isRepresentativeFilled = binding.etPartnerRepresentative.text?.isNotBlank() == true
        val isAddressFilled = binding.etPartnerAddress.text?.isNotBlank() == true
        val isDetailFilled = binding.etPartnerAddressDetail.text?.isNotBlank() == true

        val allValid = isNameFilled && isBusinessNumberFilled && isRepresentativeFilled && 
                      isAddressFilled && isDetailFilled && isAddressSearchClicked
        setButtonEnabled(allValid)
    }

    private fun setButtonEnabled(enabled: Boolean) {
        binding.btnCompleted.isEnabled = enabled
        binding.btnCompleted.background = ContextCompat.getDrawable(
            requireContext(),
            if (enabled) R.drawable.btn_basic_selected else R.drawable.btn_basic_unselected
        )
    }
    
    private fun savePartnerInfoToViewModel() {
        // 대학교는 기본적으로 SSU로 설정
        signUpViewModel.setUniversity("SSU")
        
        // 부서와 전공은 기본값으로 설정 (제휴업체는 해당 없음)
        signUpViewModel.setDepartment("PARTNER")
        signUpViewModel.setMajor("PARTNER")
        
        // 제휴업체 정보 설정
        val companyName = binding.etPartnerName.text.toString().trim()
        val businessNumber = binding.etPartnerBusinessNumber.text.toString().trim()
        val representativeName = binding.etPartnerRepresentative.text.toString().trim()
        val detailAddress = binding.etPartnerAddressDetail.text.toString().trim()
        
        signUpViewModel.setCompanyName(companyName)
        signUpViewModel.setBusinessNumber(businessNumber)
        signUpViewModel.setRepresentativeName(representativeName)
        signUpViewModel.setDetailAddress(detailAddress)
        
        // 선택된 장소 정보는 주소 검색에서 설정됨
        // TODO: 주소 검색 결과를 SelectedPlaceDto로 변환하여 설정
    }
}
