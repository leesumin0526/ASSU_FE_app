package com.example.assu_fe_app.presentation.admin.signup

import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentAdminSignUpInfoBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.util.setProgressBarFillAnimated

class AdminSignUpInfoFragment :
    BaseFragment<FragmentAdminSignUpInfoBinding>(R.layout.fragment_admin_sign_up_info) {

    private var isAddressSearchClicked = false

    override fun initObserver() {}

    override fun initView() {
        binding.ivSignupProgressBar.setProgressBarFillAnimated(
            container = binding.flSignupProgressContainer,
            fromPercent = 0.55f,
            toPercent = 0.7f
        )
        // 상세주소 입력 비활성화
        binding.etAdminAddressDetail.isEnabled = false

        // 돋보기 버튼 클릭 시 주소 자동 입력 + 상세주소 활성화
        binding.btnAdminAddressSearch.setOnClickListener {
            binding.etAdminAddress.setText("서울특별시 동작구 상도로 369")
            binding.etAdminAddressDetail.isEnabled = true
            isAddressSearchClicked = true
            checkAllInputs()
        }

        // 텍스트 변경 감지
        binding.etAdminName.addTextChangedListener { checkAllInputs() }
        binding.etAdminAddressDetail.addTextChangedListener { checkAllInputs() }

        // 버튼 클릭
        binding.btnCompleted.setOnClickListener {
            if (binding.btnCompleted.isEnabled) {
                findNavController().navigate(R.id.action_admin_info_to_seal)
            }
        }

        // 초기 상태
        setButtonEnabled(false)
    }

    private fun checkAllInputs() {
        val isNameFilled = binding.etAdminName.text?.isNotBlank() == true
        val isAddressFilled = binding.etAdminAddress.text?.isNotBlank() == true
        val isDetailFilled = binding.etAdminAddressDetail.text?.isNotBlank() == true

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
