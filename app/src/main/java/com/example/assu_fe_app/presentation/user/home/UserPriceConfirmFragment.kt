package com.example.assu_fe_app.presentation.user.home

import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentUserPriceConfirmBinding
import com.example.assu_fe_app.presentation.base.BaseFragment


class UserPriceConfirmFragment : BaseFragment<FragmentUserPriceConfirmBinding>(R.layout.fragment_user_price_confirm) {
    override fun initObserver() {

    }

    override fun initView() {
        binding.btnPriceBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

}