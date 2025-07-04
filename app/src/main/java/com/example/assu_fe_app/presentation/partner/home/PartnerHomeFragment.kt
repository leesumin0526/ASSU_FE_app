package com.example.assu_fe_app.presentation.partner.home

import androidx.navigation.Navigation
import com.example.assu_fe_app.presentation.common.contract.PartnershipContentDialogFragment
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.partner_admin.home.PartnershipContractItem
import com.example.assu_fe_app.databinding.FragmentPartnerHomeBinding
import com.example.assu_fe_app.presentation.base.BaseFragment

class PartnerHomeFragment :
    BaseFragment<FragmentPartnerHomeBinding>(R.layout.fragment_partner_home) {

    override fun initObserver() {
    }

    override fun initView() {
        binding.btnPartnerHomeViewAll.setOnClickListener { view ->
            Navigation.findNavController(view).navigate(R.id.action_partner_home_to_partner_view_admin_list)
        }
        binding.partnerHomeListItem1.setOnClickListener {
            val dialog = PartnershipContentDialogFragment(dummyItem)
            dialog.show(parentFragmentManager, "PartnershipContentDialog")
        }
        binding.partnerHomeListItem2.setOnClickListener {
            val dialog = PartnershipContentDialogFragment(dummyItem)
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