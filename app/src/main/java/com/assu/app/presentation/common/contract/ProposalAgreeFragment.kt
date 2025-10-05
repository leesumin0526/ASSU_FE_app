package com.assu.app.presentation.common.contract

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.assu.app.R
import com.assu.app.data.dto.partner_admin.home.PartnershipContractItem
import com.assu.app.data.dto.partnership.response.CriterionType
import com.assu.app.data.dto.partnership.response.OptionType
import com.assu.app.databinding.FragmentProposalAgreeBinding
import com.assu.app.domain.model.partnership.ProposalPartnerDetailsModel
import com.assu.app.presentation.base.BaseFragment
import com.assu.app.presentation.common.chatting.ChattingSentProposalFragment
import com.assu.app.presentation.common.chatting.proposal.adapter.ProposalModifyAdapter
import com.assu.app.ui.partnership.PartnershipViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import kotlin.text.ifEmpty

@AndroidEntryPoint
class ProposalAgreeFragment : BaseFragment<FragmentProposalAgreeBinding>(R.layout.fragment_proposal_agree) {
    private val partnershipViewModel: PartnershipViewModel by activityViewModels()
    private lateinit var proposalAdapter: ProposalModifyAdapter

    private var paperId: Long = -1L
    private var partnerId: Long = -1L

    override fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            partnershipViewModel.summaryText.collect { text ->
                if (text.isNotEmpty()) {
                    binding.tvPartnershipContentSignBox.text = text
                }
            }
        }
        partnershipViewModel.partnershipDetailLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PartnershipViewModel.PartnershipDetailUiState.Idle -> {
                    hideLoading()
                }
                is PartnershipViewModel.PartnershipDetailUiState.Loading -> {
                    showLoading("로딩 중...")
                }
                is PartnershipViewModel.PartnershipDetailUiState.Success -> {
                    hideLoading()
                    displayProposalData(state.data)
                }
                is PartnershipViewModel.PartnershipDetailUiState.Fail -> {
                    hideLoading()
                    Toast.makeText(requireContext(), "조회 실패", Toast.LENGTH_SHORT).show()
                }
                is PartnershipViewModel.PartnershipDetailUiState.Error -> {
                    hideLoading()
                    Toast.makeText(requireContext(), "오류 발생", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            partnershipViewModel.updatePartnershipStatusUiState.collect { state ->
                when (state) {
                    is PartnershipViewModel.UpdatePartnershipStatusUiState.Idle -> {
                    }
                    is PartnershipViewModel.UpdatePartnershipStatusUiState.Loading -> {
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
                }
            }
        }
    }

    override fun initView() {
        extractArguments()
        initializeUI()

        loadProposalData()
    }

    private fun extractArguments() {
        arguments?.let { bundle ->
            paperId = bundle.getLong("paperId", -1L)
            partnerId = bundle.getLong("partnerId", -1L)
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

        // 동의하기 버튼
        binding.llAgree.setOnClickListener {
            handleAgreeButtonClick()
        }
        binding.btnModify.setOnClickListener {
            handleAgreeButtonClick()
        }
    }

    private fun showLoading(message: String = "로딩 중...") {
        binding.loadingOverlay.visibility = View.VISIBLE
        binding.tvLoadingText.text = message
    }

    private fun hideLoading() {
        binding.loadingOverlay.visibility = View.GONE
    }

    private fun loadProposalData() {
        if (paperId > 0) {
            partnershipViewModel.getPartnershipDetail(paperId)
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
                        items = option.goods.joinToString(", ") { it.goodsName },
                        category = option.category.ifEmpty { null }
                    )
                }

                option.optionType == OptionType.SERVICE && option.criterionType == CriterionType.PRICE -> {
                    PartnershipContractItem.Service.ByAmount(
                        minAmount = changeLongToMoney(option.cost),
                        items = option.goods.joinToString(", ") { it.goodsName },
                        category = option.category.ifEmpty { null }
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
                        minAmount = changeLongToMoney(option.cost),
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

        if (paperId > 0) {
            partnershipViewModel.updatePartnershipStatus(paperId, "ACTIVE")
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

    private fun changeLongToMoney(cost: Long?): String {
        val formatter = NumberFormat.getNumberInstance(Locale.KOREA)
        return formatter.format(cost)
    }
}