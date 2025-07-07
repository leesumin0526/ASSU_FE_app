package com.example.assu_fe_app.presentation.user.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentPriceConfirmBinding
import com.example.assu_fe_app.presentation.base.BaseFragment


class PriceConfirmFragment : BaseFragment<FragmentPriceConfirmBinding>(R.layout.fragment_price_confirm) {
    override fun initObserver() {

    }

    override fun initView() {
        binding.btnPriceBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}