package com.ssu.assu.presentation.user.home

import androidx.fragment.app.activityViewModels
import com.ssu.assu.R
import com.ssu.assu.data.dto.usage.SaveUsageRequestDto
import com.ssu.assu.databinding.FragmentUserPriceConfirmBinding
import com.ssu.assu.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class UserPriceConfirmFragment : BaseFragment<FragmentUserPriceConfirmBinding>(R.layout.fragment_user_price_confirm) {

    private val viewModel: UserVerifyViewModel by activityViewModels()

    override fun initObserver() {
        viewModel.selectedContent.observe(viewLifecycleOwner) { content ->
            binding.tvConfirmPrice.text = content?.cost.toString()
        }
    }

    override fun initView() {
        binding.btnPriceBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnPriceConfirm.setOnClickListener {
            viewModel.postPersonalUsageData(
                SaveUsageRequestDto(
                    viewModel.storeId,
                    viewModel.tableNumber,
                    viewModel.selectedAdminName,
                    viewModel.selectedContentId,
                    0,
                    viewModel.selectedPaperContent,
                    viewModel.storeName.value.toString(),
                    emptyList()

                )
            )
            val nextFragment = UserPartnershipVerifyCompleteFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, nextFragment)
                .addToBackStack(null)
                .commit()
        }
    }

}