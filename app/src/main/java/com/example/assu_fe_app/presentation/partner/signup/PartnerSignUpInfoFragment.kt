package com.example.assu_fe_app.presentation.partner.signup

import android.content.Intent
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentPartnerSignUpInfoBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.location.LocationSearchActivity
import com.example.assu_fe_app.ui.auth.SignUpViewModel
import com.example.assu_fe_app.util.setProgressBarFillAnimated

class PartnerSignUpInfoFragment :
    BaseFragment<FragmentPartnerSignUpInfoBinding>(R.layout.fragment_partner_sign_up_info) {

    private val signUpViewModel: SignUpViewModel by activityViewModels()
    private var isAddressSearchClicked = false

    // 주소 검색 결과를 받기 위한 ActivityResultLauncher
    private val addressSearchLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == LocationSearchActivity.RESULT_CODE_ADDRESS_SELECTED) {
            val selectedAddress = result.data?.getStringExtra(LocationSearchActivity.EXTRA_SELECTED_ADDRESS)
            selectedAddress?.let { address ->
                binding.etPartnerAddress.setText(address)
                Log.d("PartnerSignUpInfoFragment", "받은 주소: $address")
            }
        }
    }

    override fun initObserver() {}

    override fun initView() {

        binding.ivSignupProgressBar.setProgressBarFillAnimated(
            container = binding.flSignupProgressContainer,
            fromPercent = 0.55f,
            toPercent = 0.7f
        )
        // 상세주소 입력 비활성화 초기화
        binding.etPartnerAddressDetail.isEnabled = false

        // 주소 돋보기 클릭 시 주소 검색 Activity 실행
        binding.btnPartnerAddressSearch.setOnClickListener {
            val intent = Intent(requireContext(), LocationSearchActivity::class.java)
            addressSearchLauncher.launch(intent)

            binding.etPartnerAddressDetail.isEnabled = true
            isAddressSearchClicked = true
            checkAllInputs()
        }

        // 텍스트 변경 감지 리스너 등록
        binding.etPartnerName.addTextChangedListener { checkAllInputs() }
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
        val isAddressFilled = binding.etPartnerAddress.text?.isNotBlank() == true
        val isDetailFilled = binding.etPartnerAddressDetail.text?.isNotBlank() == true

        val allValid = isNameFilled && isAddressFilled && isDetailFilled && isAddressSearchClicked
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
        
        // 제휴업체 정보 설정
        val companyName = binding.etPartnerName.text.toString().trim()
        val detailAddress = binding.etPartnerAddressDetail.text.toString().trim()
        
        signUpViewModel.setCompanyName(companyName)
        signUpViewModel.setDetailAddress(detailAddress)
        
        // 선택된 장소 정보는 주소 검색에서 설정됨
        // TODO: 주소 검색 결과를 SelectedPlaceDto로 변환하여 설정
    }
}
