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
import com.example.assu_fe_app.data.local.AuthTokenLocalStore
import com.example.assu_fe_app.databinding.FragmentServiceProposalWritingBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.chatting.proposal.adapter.ServiceProposalAdapter
import com.example.assu_fe_app.ui.partnership.PartnershipViewModel
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ServiceProposalWritingFragment
    : BaseFragment<FragmentServiceProposalWritingBinding>(R.layout.fragment_service_proposal_writing) {

    private val viewModel: PartnershipViewModel by activityViewModels()
    private lateinit var adapter: ServiceProposalAdapter
    @Inject lateinit var authTokenLocalStore: AuthTokenLocalStore

    private var isEditMode: Boolean = false

    override fun initView() {
        binding.lifecycleOwner = viewLifecycleOwner

        // ✅ arguments에서 수정 모드 여부 확인
        arguments?.let { bundle ->
            isEditMode = bundle.getBoolean("isEditMode", false)
            Log.d("ServiceProposalWritingFragment", "Edit mode: $isEditMode")
        }

        adapter = ServiceProposalAdapter (onItemEvent = viewModel::onBenefitEvent)
        binding.rvFragmentServiceProposalItemSet.adapter = adapter
        binding.rvFragmentServiceProposalItemSet.layoutManager = LinearLayoutManager(requireContext())

        val userRole = authTokenLocalStore.getUserRole()
        Log.d("RoleCheck", "Current user role is: $userRole")

//        if (userRole.equals("ADMIN", ignoreCase = true)) {
//            // --- 관리자(ADMIN)일 경우 ---
//            // 1. 제휴 제안인(본인) 이름 설정 및 비활성화
//            viewModel.adminName.value = tokenManager.getLoginModel()?.username ?: ""
//            binding.tvFragmentServiceProposalAdmin.isEnabled = false
//
//            // 2. 제휴 업체는 hint만 보여주고, 클릭 시 검색 화면으로 이동
//            binding.tvFragmentServiceProposalPartner.hint = "업체명을 입력해주세요"
//            binding.tvFragmentServiceProposalPartner.setOnClickListener {
//                findNavController().navigate(
//                    R.id.action_serviceProposalWritingFragment_to_locationSearchFragment,
//                    Bundle().apply { putString("type", "passive") }
//                )
//            }
//
//        } else { // PARTNER일 경우
//            // --- 파트너(PARTNER)일 경우 ---
//            arguments?.let { bundle ->
//                val partnerId = bundle.getLong("partnerId", -1L)
//                val paperId = bundle.getLong("paperId", -1L)
//                val adminName = bundle.getString("adminName", "")
////                val partnerName = bundle.getString("partnerName", "")
//                val partnerName = "제휴 업체" // TODO : 임시 하드코딩
//
//                Log.d("BundleCheck", "partnerName from bundle: '$partnerName', adminName from bundle: '$adminName'")
//
//                if (partnerId != -1L && paperId != -1L) {
//                    viewModel.initProposalData(partnerId, paperId)
//                    // 1. ViewModel의 StateFlow 업데이트
//                    viewModel.adminName.value = adminName
//                    viewModel.partnerName.value = partnerName
//                }
//            }
//            // 2. 클릭 기능 비활성화
//            binding.tvFragmentServiceProposalAdmin.isEnabled = false
//            binding.tvFragmentServiceProposalPartner.isEnabled = false
//        }

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
                        binding.btnCompleted.backgroundTintList = ContextCompat.getColorStateList(requireContext(), colorRes)
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
        fun newInstance(partnerId: Long, paperId: Long, adminName: String, partnerName: String): ServiceProposalWritingFragment {
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
        fun newInstanceForEdit(partnerId: Long, paperId: Long, isEditMode: Boolean = true, adminName: String, partnerName: String): ServiceProposalWritingFragment {
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
