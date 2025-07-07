package com.example.assu_fe_app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.databinding.FragmentContractPassiveRegisterBinding
import com.example.assu_fe_app.presentation.base.BaseFragment


class ContractPassiveRegisterFragment : BaseFragment<FragmentContractPassiveRegisterBinding>(R.layout.fragment_contract_passive_register) {
    override fun initObserver() {
    }

    override fun initView() {
        binding.ivFragmentContractPassiveRegisterBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}