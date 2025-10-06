package com.ssu.assu.presentation.common.chatting.proposal

import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssu.assu.R
import com.ssu.assu.data.local.AuthTokenLocalStore
import com.ssu.assu.databinding.FragmentServiceProposalWritingBinding
import com.ssu.assu.presentation.base.BaseFragment
import com.ssu.assu.presentation.common.chatting.proposal.adapter.ServiceProposalAdapter
import com.ssu.assu.ui.partnership.PartnershipViewModel
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ServiceProposalWritingFragment
    : BaseFragment<FragmentServiceProposalWritingBinding>(R.layout.fragment_service_proposal_writing) {

    private val viewModel: PartnershipViewModel by activityViewModels()
    private lateinit var adapter: ServiceProposalAdapter

    @Inject
    lateinit var authTokenLocalStore: AuthTokenLocalStore

    private var isEditMode: Boolean = false

    override fun initView() {
        binding.lifecycleOwner = viewLifecycleOwner

        // ✅ arguments에서 수정 모드 여부 확인
        arguments?.let { bundle ->
            isEditMode = bundle.getBoolean("isEditMode", false)
            Log.d("ServiceProposalWritingFragment", "Edit mode: $isEditMode")
        }

        adapter = ServiceProposalAdapter(onItemEvent = viewModel::onBenefitEvent)
        binding.rvFragmentServiceProposalItemSet.adapter = adapter
        binding.rvFragmentServiceProposalItemSet.layoutManager =
            LinearLayoutManager(requireContext())

        val userRole = authTokenLocalStore.getUserRole()
        Log.d("RoleCheck", "Current user role is: $userRole")

        if (userRole.equals("ADMIN", ignoreCase = true)) {
            setupAdminMode()
        } else {
            setupPartnerMode()
        }

        binding.tvAddProposalItem.setOnClickListener {
            viewModel.addBenefitItem()
        }

        binding.btnCompleted.setOnClickListener {
            if (isEditMode) {
                // ✅ 수정 모드일 때는 바로 TermWritingFragment로 이동
                navigateToTermWriting()
            } else {
                // ✅ 일반 모드일 때는 기존 로직
                try {
                    if (findNavController().currentDestination?.id == R.id.serviceProposalWritingFragment) {
                        findNavController().navigate(
                            R.id.action_serviceProposalWritingFragment_to_serviceProposalTermWritingFragment
                        )
                    } else {
                        navigateToTermWriting()
                    }
                } catch (e: Exception) {
                    Log.e("ServiceProposalWritingFragment", "Navigation error", e)
                    navigateToTermWriting()
                }
            }
        }

        binding.ivFragmentServiceProposalBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    // ✅ TermWritingFragment로 이동하는 공통 함수
    private fun navigateToTermWriting() {
        val fragment = if (isEditMode) {
            ServiceProposalTermWritingFragment.newInstanceForEdit()
        } else {
            ServiceProposalTermWritingFragment()
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.chatting_fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun setupAdminMode() {
        viewModel.adminName.value = authTokenLocalStore.getUserName() ?: ""
        binding.tvFragmentServiceProposalAdmin.isEnabled = false

        binding.tvFragmentServiceProposalPartner.hint = "업체명을 입력해주세요"
        binding.tvFragmentServiceProposalPartner.setOnClickListener {
            try {
                findNavController().navigate(
                    R.id.action_serviceProposalWritingFragment_to_locationSearchFragment,
                    Bundle().apply { putString("type", "passive") }
                )
            } catch (e: Exception) {
                Log.e("ServiceProposalWritingFragment", "Location search navigation error", e)
            }
        }
    }

    private fun setupPartnerMode() {
        arguments?.let { bundle ->
            val partnerId = bundle.getLong("partnerId", -1L)
            val paperId = bundle.getLong("paperId", -1L)
            val adminName = bundle.getString("adminName", "")
            val partnerName = authTokenLocalStore.getUserName() ?: "-"

            Log.d("BundleCheck", "partnerName: '$partnerName', adminName: '$adminName'")

            if (partnerId != -1L && paperId != -1L) {
                viewModel.initProposalData(partnerId, paperId)
                viewModel.adminName.value = adminName
                viewModel.partnerName.value = partnerName
            }
        }

        binding.tvFragmentServiceProposalAdmin.isEnabled = false
        binding.tvFragmentServiceProposalPartner.isEnabled = false
    }

    override fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.benefitItems.collect { items ->
                        adapter.submitList(items)
                    }
                }
                launch {
                    viewModel.isNextButtonEnabled.collect { isEnabled ->
                        binding.btnCompleted.isEnabled = isEnabled
                        val colorRes = if (isEnabled) R.color.assu_main else R.color.assu_sub
                        binding.btnCompleted.backgroundTintList =
                            ContextCompat.getColorStateList(requireContext(), colorRes)
                    }
                }
                launch {
                    viewModel.adminName.collect { name ->
                        if (binding.tvFragmentServiceProposalAdmin.text.toString() != name) {
                            binding.tvFragmentServiceProposalAdmin.setText(name)
                        }
                    }
                }
                launch {
                    viewModel.partnerName.collect { name ->
                        if (binding.tvFragmentServiceProposalPartner.text.toString() != name) {
                            binding.tvFragmentServiceProposalPartner.setText(name)
                        }
                    }
                }
            }
        }
    }

    companion object {
        // ✅ 기존 newInstance (일반 모드)
        fun newInstance(
            partnerId: Long,
            paperId: Long,
            adminName: String,
            partnerName: String
        ): ServiceProposalWritingFragment {
            return ServiceProposalWritingFragment().apply {
                arguments = Bundle().apply {
                    putLong("partnerId", partnerId)
                    putLong("paperId", paperId)
                    putString("adminName", adminName)
                    putString("partnerName", partnerName)
                    putBoolean("isEditMode", false)
                }
            }
        }

        // ✅ 수정 모드용 newInstance
        fun newInstanceForEdit(
            partnerId: Long,
            paperId: Long,
            isEditMode: Boolean = true,
            adminName: String,
            partnerName: String
        ): ServiceProposalWritingFragment {
            return ServiceProposalWritingFragment().apply {
                arguments = Bundle().apply {
                    putLong("partnerId", partnerId)
                    putLong("paperId", paperId)
                    putString("adminName", adminName)
                    putString("partnerName", partnerName)
                    putBoolean("isEditMode", isEditMode)
                }
            }
        }
    }
}
