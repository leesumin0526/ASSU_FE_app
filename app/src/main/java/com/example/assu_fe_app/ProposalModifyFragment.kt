package com.example.assu_fe_app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assu_fe_app.data.local.AuthTokenLocalStore
import com.example.assu_fe_app.databinding.FragmentProposalModifyBinding
import com.example.assu_fe_app.presentation.common.chatting.proposal.ServiceProposalWritingFragment
import com.example.assu_fe_app.presentation.common.chatting.proposal.adapter.ProposalModifyAdapter
import com.example.assu_fe_app.presentation.common.chatting.proposal.adapter.ServiceProposalAdapter
import com.example.assu_fe_app.presentation.common.contract.ViewMode
import com.example.assu_fe_app.ui.partnership.PartnershipViewModel
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class ProposalModifyFragment: Fragment(){

    private var _binding: FragmentProposalModifyBinding? = null
    private val binding get() = _binding!!

    private val partnershipViewModel: PartnershipViewModel by activityViewModels()
    @Inject lateinit var tokenManager: AuthTokenLocalStore

    private var entryType: ViewMode = ViewMode.MODIFY
    private var partnerId: Long = -1L
    private var paperId: Long = -1L
    private var partnershipId: Long? = null

    private lateinit var proposalAdapter: ProposalModifyAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProposalModifyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        extractArgumentsData()
        initializeUI()
        setupObservers()
        loadProposalData()
    }

    private fun extractArgumentsData() {
        arguments?.let { bundle ->
            val entryTypeString = bundle.getString("entryType", ViewMode.MODIFY.name)
            entryType = ViewMode.valueOf(entryTypeString)
            partnerId = bundle.getLong("partnerId", -1L)
            paperId = bundle.getLong("paperId", -1L)
            partnershipId = bundle.getLong("partnershipId", -1L).takeIf { it != -1L }

            Log.d("ProposalModifyFragment", "Entry type: $entryType, partnerId: $partnerId, paperId: $paperId")
        }
    }

    private fun initializeUI() {
        setupUIByEntryType()

        proposalAdapter = ProposalModifyAdapter { position, item ->
            Log.d("ProposalModifyFragment", "Proposal item clicked: $position")
        }

        binding.rvPartnershipContentList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = proposalAdapter
        }

        binding.ivPartnershipContentCross.setOnClickListener {
            handleBackPress()
        }

        binding.llPartnershipCheck.setOnClickListener {
            handleBottomButtonClick()
        }
    }

    private fun setupUIByEntryType() {
        when (entryType) {
            ViewMode.MODIFY -> {
                binding.llPartnershipCheck.setBackgroundResource(R.drawable.bg_chatting_proposal_box)
                binding.ivPartnershipModify.visibility = View.VISIBLE
                binding.tvPartnershipModify.text = "수정하기"
            }
            ViewMode.QR_SAVE -> {
                binding.llPartnershipCheck.setBackgroundResource(R.drawable.bg_chatting_call_box)
                binding.ivPartnershipModify.visibility = View.GONE
                binding.tvPartnershipModify.text = "QR저장"
            }
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            partnershipViewModel.getPartnershipDetailUiState.collect { state ->
                when (state) {
                    is PartnershipViewModel.PartnershipDetailUiState.Loading -> {
                        showLoading(true)
                    }
                    is PartnershipViewModel.PartnershipDetailUiState.Success -> {
                        showLoading(false)
                        updateUIWithProposalData(state.data)
                    }
                    is PartnershipViewModel.PartnershipDetailUiState.Fail -> {
                        showLoading(false)
                        showToast("제안서 조회 실패: ${state.message}")
                    }
                    is PartnershipViewModel.PartnershipDetailUiState.Error -> {
                        showLoading(false)
                        showToast("오류가 발생했습니다: ${state.message}")
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun loadProposalData() {
        when (entryType) {
            ViewMode.MODIFY -> {
                loadFromViewModel()
            }
            ViewMode.QR_SAVE -> {
                partnershipId?.let { id ->
                    partnershipViewModel.getPartnershipDetail(id)
                } ?: run {
                    showToast("제휴 정보가 없습니다.")
                }
            }
        }
    }

    private fun loadFromViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            val partnerName = partnershipViewModel.partnerName.value
            val adminName = partnershipViewModel.adminName.value
            val startDate = partnershipViewModel.partnershipStartDate.value
            val endDate = partnershipViewModel.partnershipEndDate.value
            val benefitItems = partnershipViewModel.benefitItems.value

            binding.etFragmentServiceProposalPartner.setText(partnerName)
            binding.etFragmentServiceProposalAdmin.setText(adminName)
            binding.tvPartnershipContentStartDate.text = startDate
            binding.tvPartnershipContentEndDate.text = endDate

            proposalAdapter.submitList(benefitItems.map { it.toProposalModifyItem() })
        }
    }

    private fun updateUIWithProposalData(data: Any) {
        Log.d("ProposalModifyFragment", "Updating UI with API data: $data")
        // TODO: 실제 데이터 구조에 맞게 구현
    }

    private fun handleBackPress() {
        parentFragmentManager.popBackStack()
    }

    // ✅ 하단 버튼 클릭 처리 수정
    private fun handleBottomButtonClick() {
        when (entryType) {
            ViewMode.MODIFY -> {
                // ✅ 수정하기 버튼 클릭 시 ServiceProposalWritingFragment로 이동
                navigateToEditMode()
            }
            ViewMode.QR_SAVE -> {
                navigateToQrSave()
            }
        }
    }

    // ✅ 수정 모드로 이동 - ServiceProposalWritingFragment로 이동
    private fun navigateToEditMode() {
        val fragment = ServiceProposalWritingFragment.newInstanceForEdit(
            partnerId = partnerId,
            paperId = paperId,
            isEditMode = true // 수정 모드임을 표시
        )

        parentFragmentManager.beginTransaction()
            .replace(R.id.chatting_fragment_container, fragment)
            .addToBackStack(null)
            .commit()

        Log.d("ProposalModifyFragment", "Navigate to ServiceProposalWritingFragment for editing")
    }

    private fun navigateToQrSave() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.main, QrSaveFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun showLoading(show: Boolean) {
        Log.d("ProposalModifyFragment", "Loading: $show")
    }

    private fun showToast(message: String) {
        android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(
            entryType: ViewMode,
            partnerId: Long,
            paperId: Long
        ): ProposalModifyFragment {
            return ProposalModifyFragment().apply {
                arguments = Bundle().apply {
                    putString("entryType", entryType.name)
                    putLong("partnerId", partnerId)
                    putLong("paperId", paperId)
                }
            }
        }

        fun newInstanceWithPartnershipId(
            entryType: ViewMode,
            partnershipId: Long
        ): ProposalModifyFragment {
            return ProposalModifyFragment().apply {
                arguments = Bundle().apply {
                    putString("entryType", entryType.name)
                    putLong("partnershipId", partnershipId)
                }
            }
        }
    }
}