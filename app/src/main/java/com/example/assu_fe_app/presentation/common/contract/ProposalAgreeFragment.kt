package com.example.assu_fe_app.presentation.common.contract

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.partner_admin.home.PartnershipContractItem
import com.example.assu_fe_app.data.dto.partnership.response.CriterionType
import com.example.assu_fe_app.data.dto.partnership.response.OptionType
import com.example.assu_fe_app.databinding.FragmentProposalAgreeBinding
import com.example.assu_fe_app.domain.model.partnership.ProposalPartnerDetailsModel
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.chatting.ChattingSentProposalFragment
import com.example.assu_fe_app.presentation.common.chatting.proposal.adapter.ProposalModifyAdapter
import com.example.assu_fe_app.ui.partnership.PartnershipViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.text.ifEmpty

@AndroidEntryPoint
class ProposalAgreeFragment : BaseFragment<FragmentProposalAgreeBinding>(R.layout.fragment_proposal_agree) {
    private val partnershipViewModel: PartnershipViewModel by activityViewModels()
    private lateinit var proposalAdapter: ProposalModifyAdapter

    private var paperId: Long = -1L
    private var partnerId: Long = -1L
    private var partnershipId: Long? = null

    override fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            partnershipViewModel.getPartnershipDetailUiState.collect { state ->
                when (state) {
                    is PartnershipViewModel.PartnershipDetailUiState.Loading -> {
                        // 로딩 표시
                    }
                    is PartnershipViewModel.PartnershipDetailUiState.Success -> {
                        displayProposalData(state.data)
                    }
                    is PartnershipViewModel.PartnershipDetailUiState.Fail -> {
                        Toast.makeText(requireContext(), "조회 실패", Toast.LENGTH_SHORT).show()
                    }
                    is PartnershipViewModel.PartnershipDetailUiState.Error -> {
                        Toast.makeText(requireContext(), "오류 발생", Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            partnershipViewModel.updatePartnershipStatusUiState.collect { state ->
                when (state) {
                    is PartnershipViewModel.UpdatePartnershipStatusUiState.Loading -> {
                        // 로딩 표시
                        binding.btnModify.isEnabled = false
                    }
                    is PartnershipViewModel.UpdatePartnershipStatusUiState.Success -> {
                        // 성공 시 체결 완료 화면으로 이동
                        navigateToSuccessScreen()
                    }
                    is PartnershipViewModel.UpdatePartnershipStatusUiState.Fail -> {
                        Toast.makeText(requireContext(), "제휴 체결 실패: ${state.message}", Toast.LENGTH_SHORT).show()
                        binding.btnModify.isEnabled = true
                    }
                    is PartnershipViewModel.UpdatePartnershipStatusUiState.Error -> {
                        Toast.makeText(requireContext(), "오류 발생: ${state.message}", Toast.LENGTH_SHORT).show()
                        binding.btnModify.isEnabled = true
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun initView() {
        extractArguments()
        initializeUI()
        initObserver()
        loadProposalData()
    }

    private fun extractArguments() {
        arguments?.let { bundle ->
            paperId = bundle.getLong("paperId", -1L)
            partnerId = bundle.getLong("partnerId", -1L)
            partnershipId = bundle.getLong("partnershipId", -1L).takeIf { it != -1L }
        }
    }

    private fun initializeUI() {
        // EditText 읽기 전용 설정
        binding.etFragmentServiceProposalPartner.isEnabled = false
        binding.etFragmentServiceProposalAdmin.isEnabled = false

        // X 버튼 - ChattingActivity로 돌아가기
        binding.ivPartnershipContentCross.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        val adminName = partnershipViewModel.adminName.value
        val partnerName = partnershipViewModel.partnerName.value
        val signDate = partnershipViewModel.signDate.value
        val summaryText = buildString {
            append("위와 같이 ")
            append(adminName.ifEmpty { "-" })
            append("와의\n제휴를 제안합니다.\n\n")
            append(signDate + "\n")
            append("대표 ")
            append(partnerName.ifEmpty { "-" })
        }
        binding.tvPartnershipContentSignBox.text = summaryText

        // 동의하기 버튼
        binding.llAgree.setOnClickListener {
            handleAgreeButtonClick()
        }
        binding.btnModify.setOnClickListener {
            handleAgreeButtonClick()
        }
    }

    private fun loadProposalData() {
        val idToUse = partnershipId ?: paperId.takeIf { it != -1L }
        if (idToUse != null && idToUse > 0) {
            partnershipViewModel.getPartnershipDetail(idToUse)
        }
    }

    private fun displayProposalData(data: ProposalPartnerDetailsModel) {
        // partnerName과 adminName은 ViewModel에서 가져오기
        val partnerName = partnershipViewModel.partnerName.value
        val adminName = partnershipViewModel.adminName.value

        binding.etFragmentServiceProposalPartner.setText(partnerName)
        binding.etFragmentServiceProposalAdmin.setText(adminName)
        binding.tvPartnershipContentStartDate.text = data.periodStart
        binding.tvPartnershipContentEndDate.text = data.periodEnd

        // options를 PartnershipContractItem으로 변환
        val items = data.options.mapNotNull { option ->
            when {
                option.optionType == OptionType.SERVICE && option.criterionType == CriterionType.HEADCOUNT -> {
                    PartnershipContractItem.Service.ByPeople(
                        minPeople = option.people,
                        items = if (option.goods.isNotEmpty()) {
                            option.goods.joinToString(", ") { it.goodsName }
                        } else {
                            option.category
                        }
                    )
                }

                option.optionType == OptionType.SERVICE && option.criterionType == CriterionType.PRICE -> {
                    PartnershipContractItem.Service.ByAmount(
                        minAmount = option.cost.toInt(),
                        items = if (option.goods.isNotEmpty()) {
                            option.goods.joinToString(", ") { it.goodsName }
                        } else {
                            option.category
                        }
                    )
                }

                option.optionType == OptionType.DISCOUNT && option.criterionType == CriterionType.HEADCOUNT -> {
                    PartnershipContractItem.Discount.ByPeople(
                        minPeople = option.people,
                        percent = option.discountRate.toInt()
                    )
                }

                option.optionType == OptionType.DISCOUNT && option.criterionType == CriterionType.PRICE -> {
                    PartnershipContractItem.Discount.ByAmount(
                        minAmount = option.cost.toInt(),
                        percent = option.discountRate.toInt()
                    )
                }
                else -> null
            }
        }

        proposalAdapter = ProposalModifyAdapter(items)
        binding.rvPartnershipContentList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = proposalAdapter
        }
    }

    private fun handleAgreeButtonClick() {
        // API 호출하여 상태를 ACTIVE로 변경
        val idToUse = partnershipId ?: paperId.takeIf { it != -1L }

        if (idToUse != null && idToUse > 0) {
            partnershipViewModel.updatePartnershipStatus(idToUse, "ACTIVE")
        } else {
            Toast.makeText(requireContext(), "제휴 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToSuccessScreen() {
        partnershipViewModel.resetUpdatePartnershipStatus()

        val fragment = ChattingSentProposalFragment.newInstance(
            paperId = paperId,
            partnerId = partnerId,
            partnershipStatus = "ACTIVE",
            isAgreementComplete = true,
            adminName = partnershipViewModel.adminName.value,
            partnerName = partnershipViewModel.partnerName.value
        )

        parentFragmentManager.beginTransaction()
            .replace(R.id.chatting_fragment_container, fragment)
            .commit()
    }

    companion object {
        fun newInstance(paperId: Long, partnerId: Long): ProposalAgreeFragment {
            return ProposalAgreeFragment().apply {
                arguments = Bundle().apply {
                    putLong("paperId", paperId)
                    putLong("partnerId", partnerId)
                }
            }
        }
    }
}