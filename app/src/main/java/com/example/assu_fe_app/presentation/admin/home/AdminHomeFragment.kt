package com.example.assu_fe_app.presentation.admin.home

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.partner_admin.home.PartnershipContractItem
import com.example.assu_fe_app.databinding.FragmentAdminHomeBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.contract.PartnershipContractDialogFragment
import com.example.assu_fe_app.presentation.common.notification.NotificationActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AdminHomeFragment :
    BaseFragment<FragmentAdminHomeBinding>(R.layout.fragment_admin_home) {
    private val vm: HomeViewModel by viewModels()

    override fun initObserver() {
    }

    override fun onResume() {
        super.onResume()
        vm.refreshBell()
    }

    override fun initView() {
        binding.btnAdminHomeViewAll.setOnClickListener { view ->
            Navigation.findNavController(view).navigate(R.id.action_admin_home_to_admin_view_partner_list)
        }

        binding.ivAdminHomeNotification.setOnClickListener {
            NotificationActivity.start(requireContext(), NotificationActivity.Role.ADMIN)
        }

        // 벨 아이콘 상태 구독
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.bellFilled.collect { exists ->
                    binding.ivAdminHomeNotification.setImageResource(
                        if (exists) R.drawable.ic_bell_fill else R.drawable.ic_bell_unfill
                    )
                }
            }
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