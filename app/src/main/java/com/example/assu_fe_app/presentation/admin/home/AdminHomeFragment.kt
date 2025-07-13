package com.example.assu_fe_app.presentation.admin.home

import androidx.navigation.Navigation
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.partner_admin.home.PartnershipContractItem
import com.example.assu_fe_app.databinding.FragmentAdminHomeBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.contract.PartnershipContractDialogFragment

class AdminHomeFragment :
    BaseFragment<FragmentAdminHomeBinding>(R.layout.fragment_admin_home) {

    override fun initObserver() {
    }

    override fun initView() {
        binding.btnAdminHomeViewAll.setOnClickListener { view ->
            Navigation.findNavController(view).navigate(R.id.action_admin_home_to_admin_view_partner_list)
        }

        binding.tvContractPassiveRegister.setOnClickListener { view ->
            Navigation.findNavController(view).navigate(R.id.action_admin_home_to_contract_passive_register)
        }

        binding.adminHomeListItem1.setOnClickListener {
            val dialog = PartnershipContractDialogFragment(dummyItem)
            dialog.show(parentFragmentManager, "PartnershipContentDialog")
        }
        binding.adminHomeListItem2.setOnClickListener {
            val dialog = PartnershipContractDialogFragment(dummyItem)
            dialog.show(parentFragmentManager, "PartnershipContentDialog")
        }
    }


    val dummyItem = listOf(
        PartnershipContractItem.Service.ByPeople(4, "캔음료"),
        PartnershipContractItem.Discount.ByPeople(4, 10),
        PartnershipContractItem.Service.ByAmount(10000, "사이다"),
        PartnershipContractItem.Discount.ByAmount(15000, 15)
    )
}