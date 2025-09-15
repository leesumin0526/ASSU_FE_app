package com.example.assu_fe_app.presentation.partner.signup

import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentPartnerSignUpInfoBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.util.setProgressBarFillAnimated

class PartnerSignUpInfoFragment :
    BaseFragment<FragmentPartnerSignUpInfoBinding>(R.layout.fragment_partner_sign_up_info) {

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
        binding.etPartnerAddressDetail.addTextChangedListener { checkAllInputs() }

        // 버튼 클릭 시 다음 화면으로 이동
        binding.btnCompleted.setOnClickListener {
            if (binding.btnCompleted.isEnabled) {
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
}
