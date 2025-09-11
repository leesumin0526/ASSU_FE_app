package com.example.assu_fe_app.presentation.user.home

import androidx.fragment.app.activityViewModels
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentUserPriceConfirmBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
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
            val nextFragment = UserPartnershipVerifyCompleteFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, nextFragment)
                .addToBackStack(null)
                .commit()
        }
    }

}