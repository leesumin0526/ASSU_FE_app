package com.ssu.assu.presentation.user.signup

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ssu.assu.R
import com.ssu.assu.databinding.FragmentUserSignUpSchoolBinding
import com.ssu.assu.databinding.FragmentUserSignUpSchoolDropDownBinding
import com.ssu.assu.presentation.base.BaseFragment
import com.ssu.assu.ui.auth.SignUpViewModel
import com.ssu.assu.util.setProgressBarFillAnimated

class UserSignUpSchoolFragment : BaseFragment<FragmentUserSignUpSchoolBinding>(R.layout.fragment_user_sign_up_school) {
    
    private val signUpViewModel: SignUpViewModel by activityViewModels()
    
    override fun initObserver() {
    }

    override fun initView() {
        binding.ivSignupProgressBar.setProgressBarFillAnimated(
            container = binding.flSignupProgressContainer,
            fromPercent = 0.40f,
            toPercent = 0.55f
        )

        // 드롭다운 트리거 클릭
        binding.tvSelectedSchool.setOnClickListener {
            showSchoolDropdown(binding.clSchoolSpinner)
        }

        // 완료 버튼 클릭 시
        binding.btnCompleted.setOnClickListener {
            val selectedSchool = binding.tvSelectedSchool.text.toString()
            if (selectedSchool == "숭실대학교") {
                signUpViewModel.setUniversity("SSU")
                findNavController().navigate(R.id.action_user_school_to_student)
            }
        }
    }

    private fun showSchoolDropdown(anchor: View) {
        val popupBinding = FragmentUserSignUpSchoolDropDownBinding.inflate(layoutInflater)
        val popupWindow = PopupWindow(
            popupBinding.root,
            anchor.width, // anchor View의 너비를 그대로 사용
            WRAP_CONTENT,
            true
        )

        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow.elevation = 10f
        popupWindow.isOutsideTouchable = true

        // 기본 문구 클릭 (학교를 선택해주세요)
        popupBinding.tvSchoolDefault.setOnClickListener {
            binding.tvSelectedSchool.text = popupBinding.tvSchoolDefault.text
            binding.tvSelectedSchool.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.assu_font_sub)
            )
            binding.btnCompleted.setBackgroundResource(R.drawable.btn_basic_unselected)
            popupWindow.dismiss()
        }

        // 숭실대학교 클릭
        popupBinding.tvSchool1.setOnClickListener {
            binding.tvSelectedSchool.text = popupBinding.tvSchool1.text
            binding.tvSelectedSchool.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.assu_font_main)
            )
            binding.btnCompleted.setBackgroundResource(R.drawable.btn_basic_selected)
            popupWindow.dismiss()
        }

        popupWindow.showAsDropDown(anchor, 0,-150)
    }

}