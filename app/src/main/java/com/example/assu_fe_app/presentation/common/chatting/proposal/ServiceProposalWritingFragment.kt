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

        arguments?.let {
            val partnerId = it.getLong("partnerId", -1L)
            val paperId = it.getLong("paperId", -1L)
            if (partnerId != -1L && paperId != -1L) {
                viewModel.initProposalData(partnerId, paperId)
            }
        }

        adapter = ServiceProposalAdapter (onItemEvent = viewModel::onBenefitEvent)
        binding.rvFragmentServiceProposalItemSet.adapter = adapter
        binding.rvFragmentServiceProposalItemSet.layoutManager = LinearLayoutManager(requireContext())

        binding.tvAddProposalItem.setOnClickListener {
            viewModel.addBenefitItem()
            Log.d("addItem", "writingFragment2")
        }

        binding.btnCompleted.setOnClickListener {
//            parentFragmentManager.beginTransaction()
//                .replace(R.id.chatting_fragment_container, ServiceProposalTermWritingFragment())
//                .addToBackStack(null) // 뒤로가기 가능하게
//                .commit()


            findNavController().navigate(
                R.id.action_serviceProposalWritingFragment_to_serviceProposalTermWritingFragment)
        }

        binding.ivFragmentServiceProposalBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

        // TODO: 확인하고 지우기
//        parentFragmentManager.setFragmentResultListener("result", this) { _, bundle ->
//            val resultData = bundle.getString("selectedPlace")
//            Log.d("SignupInfoFragment", "받은 데이터: $resultData")
//
//            binding.tvFragmentServiceProposalPartner.text = resultData
//        }



        // 파트너 위치 선택
//        binding.tvFragmentServiceProposalPartner.setOnClickListener {
//            val bundle = Bundle().apply{
//                putString("type", "passive")
//            }
//
//            findNavController().navigate(
//                R.id.action_serviceProposalWritingFragment_to_locationSearchFragment, bundle)
//        }
//
//        checkAllFieldsFilled()
//    }

    override fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.benefitItems.collect { items ->
                    adapter.submitList(items) {
                        adapter.submitList(items)
                    }
                    viewModel.isNextButtonEnabled.collect { isEnabled ->
                        binding.btnCompleted.isEnabled = isEnabled
                        val colorRes = if (isEnabled) R.color.assu_main else R.color.assu_sub
                        binding.btnCompleted.backgroundTintList = ContextCompat.getColorStateList(requireContext(), colorRes)
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
