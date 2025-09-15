package com.example.assu_fe_app.presentation.common.chatting.proposal

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentServiceProposalWritingBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.chatting.proposal.adapter.ServiceProposalAdapter
import com.example.assu_fe_app.ui.partnership.PartnershipViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ServiceProposalWritingFragment
    : BaseFragment<FragmentServiceProposalWritingBinding>(R.layout.fragment_service_proposal_writing) {

    private val viewModel: PartnershipViewModel by activityViewModels()
    private lateinit var adapter: ServiceProposalAdapter

    override fun initView() {
        binding.lifecycleOwner = viewLifecycleOwner

        adapter = ServiceProposalAdapter(
            onItemEvent = viewModel::onBenefitEvent
        )
        binding.rvFragmentServiceProposalItemSet.adapter = adapter
        binding.rvFragmentServiceProposalItemSet.layoutManager = LinearLayoutManager(requireContext())

        binding.btnCompleted.setOnClickListener {
            findNavController().navigate(R.id.action_serviceProposalWritingFragment_to_serviceProposalTermWritingFragment)
        }

        binding.ivFragmentServiceProposalBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun onItemOptionSelected() {
        binding.tvAddProposalItem.visibility = View.VISIBLE
    }

    override fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.benefitItems.collectLatest { items ->
                        adapter.submitList(items)
                    }
                }
                launch {
                    viewModel.isNextButtonEnabled.collectLatest { isEnabled ->
                        binding.btnCompleted.isEnabled = isEnabled
                        val tintRes = if (isEnabled) R.color.assu_main else R.color.assu_sub
                        binding.btnCompleted.backgroundTintList = ContextCompat.getColorStateList(requireContext(), tintRes)
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance(partnerId: Long, paperId: Long): ServiceProposalWritingFragment {
            return ServiceProposalWritingFragment().apply {
                // Bundle을 사용해 데이터를 arguments에 저장
                arguments = Bundle().apply {
                    putLong("partnerId", partnerId)
                    putLong("paperId", paperId)
                }
            }
        }
    }

}
