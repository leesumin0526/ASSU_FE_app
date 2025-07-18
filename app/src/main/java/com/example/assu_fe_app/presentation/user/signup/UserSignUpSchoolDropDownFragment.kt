package com.example.assu_fe_app.presentation.user.signup

import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentUserSignUpSchoolDropDownBinding
import com.example.assu_fe_app.presentation.base.BaseFragment

class UserSignUpSchoolDropDownFragment : BaseFragment<FragmentUserSignUpSchoolDropDownBinding>(R.layout.fragment_user_sign_up_school_drop_down) {

    private var onOptionSelected: ((String) -> Unit)? = null

    fun setOnOptionSelectedListener(listener: (String) -> Unit) {
        onOptionSelected = listener
    }

    override fun initObserver() {

    }

    override fun initView() {

        binding.tvSchool1.setOnClickListener {
            onOptionSelected?.invoke("숭실대학교")
            parentFragmentManager.popBackStack()
        }
    }

}