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
import com.example.assu_fe_app.data.manager.TokenManager
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
    @Inject lateinit var tokenManager: TokenManager

    override fun initView() {
        binding.lifecycleOwner = viewLifecycleOwner

        adapter = ServiceProposalAdapter (onItemEvent = viewModel::onBenefitEvent)
        binding.rvFragmentServiceProposalItemSet.adapter = adapter
        binding.rvFragmentServiceProposalItemSet.layoutManager = LinearLayoutManager(requireContext())

        val userRole = tokenManager.getUserRole()
        Log.d("RoleCheck", "Current user role is: $userRole")

        if (userRole.equals("ADMIN", ignoreCase = true)) {
            // --- 관리자(ADMIN)일 경우 ---
            // 1. 제휴 제안인(본인) 이름 설정 및 비활성화
            viewModel.adminName.value = tokenManager.getLoginModel()?.username ?: ""
            binding.tvFragmentServiceProposalAdmin.isEnabled = false

            // 2. 제휴 업체는 hint만 보여주고, 클릭 시 검색 화면으로 이동
            binding.tvFragmentServiceProposalPartner.hint = "업체명을 입력해주세요"
            binding.tvFragmentServiceProposalPartner.setOnClickListener {
                findNavController().navigate(
                    R.id.action_serviceProposalWritingFragment_to_locationSearchFragment,
                    Bundle().apply { putString("type", "passive") }
                )
            }

        } else { // PARTNER일 경우
            // --- 파트너(PARTNER)일 경우 ---
            arguments?.let { bundle ->
                val partnerId = bundle.getLong("partnerId", -1L)
                val paperId = bundle.getLong("paperId", -1L)
                val adminName = bundle.getString("adminName", "")
//                val partnerName = bundle.getString("partnerName", "")
                val partnerName = "제휴 업체" // TODO : 임시 하드코딩

                Log.d("BundleCheck", "partnerName from bundle: '$partnerName', adminName from bundle: '$adminName'")

                if (partnerId != -1L && paperId != -1L) {
                    viewModel.initProposalData(partnerId, paperId)
                    // 1. ViewModel의 StateFlow 업데이트
                    viewModel.adminName.value = adminName
                    viewModel.partnerName.value = partnerName
                }
            }
            // 2. 클릭 기능 비활성화
            binding.tvFragmentServiceProposalAdmin.isEnabled = false
            binding.tvFragmentServiceProposalPartner.isEnabled = false
        }

        binding.tvAddProposalItem.setOnClickListener {
            viewModel.addBenefitItem()
        }

        binding.btnCompleted.setOnClickListener {
            Log.d("ServiceProposalWritingFragment", "Complete button clicked")
            parentFragmentManager.beginTransaction()
                .replace(R.id.chatting_fragment_container, ServiceProposalTermWritingFragment())
                .addToBackStack(null) // 뒤로가기 스택에 추가
                .commit()
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
    
//        checkAllFieldsFilled()
//    }

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
        fun newInstance(partnerId: Long, paperId: Long, adminName: String, partnerName: String): ServiceProposalWritingFragment {
            return ServiceProposalWritingFragment().apply {
                // Bundle을 사용해 데이터를 arguments에 저장
                arguments = Bundle().apply {
                    putLong("partnerId", partnerId)
                    putLong("paperId", paperId)
                    putString("adminName", adminName)
                    putString("partnerName", partnerName)
                }
            }
        }
    }
}
