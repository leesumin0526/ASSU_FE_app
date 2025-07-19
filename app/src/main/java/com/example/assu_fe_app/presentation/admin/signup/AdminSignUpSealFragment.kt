package com.example.assu_fe_app.presentation.admin.signup

import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentAdminSignUpSealBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.util.setProgressBarFillAnimated

class AdminSignUpSealFragment :
    BaseFragment<FragmentAdminSignUpSealBinding>(R.layout.fragment_admin_sign_up_seal) {

    override fun initObserver() {}

    override fun initView() {
        binding.ivSignupProgressBar.setProgressBarFillAnimated(
            container = binding.flSignupProgressContainer,
            fromPercent = 0.75f,
            toPercent = 0.90f
        )
        // 처음엔 비활성화
        setButtonEnabled(false)

        // 업로드 버튼 클릭
        binding.btnUpload.setOnClickListener {
            val fakeFileName = "IMG.${(100..999).random()}"
            binding.etAdminSeal.setText(fakeFileName)
            binding.btnUpload.setImageResource(R.drawable.ic_signup_verified)
            setButtonEnabled(true)
        }

        binding.btnCompleted.setOnClickListener {
            if (binding.btnCompleted.isEnabled) {
                findNavController().navigate(R.id.action_admin_seal_to_complete)
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