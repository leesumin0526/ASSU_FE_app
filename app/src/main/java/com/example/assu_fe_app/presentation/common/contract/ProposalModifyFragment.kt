package com.example.assu_fe_app.presentation.common.contract

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assu_fe_app.QrSaveFragment
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.partner_admin.home.PartnershipContractItem
import com.example.assu_fe_app.data.dto.partnership.BenefitItem
import com.example.assu_fe_app.data.dto.partnership.PartnershipContractData
import com.example.assu_fe_app.data.dto.partnership.response.CriterionType
import com.example.assu_fe_app.data.dto.partnership.response.OptionType
import com.example.assu_fe_app.data.local.AuthTokenLocalStore
import com.example.assu_fe_app.databinding.FragmentProposalModifyBinding
import com.example.assu_fe_app.domain.model.partnership.ProposalPartnerDetailsModel
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.chatting.proposal.ServiceProposalWritingFragment
import com.example.assu_fe_app.presentation.common.chatting.proposal.adapter.ProposalModifyAdapter
import com.example.assu_fe_app.ui.partnership.PartnershipViewModel
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProposalModifyFragment: BaseFragment<FragmentProposalModifyBinding>(R.layout.fragment_proposal_modify){
    private val partnershipViewModel: PartnershipViewModel by activityViewModels()

    private lateinit var proposalAdapter: ProposalModifyAdapter
    private var contractData: PartnershipContractData? = null
    @Inject
    lateinit var authTokenLocalStore: AuthTokenLocalStore

    private var entryType: ViewMode = ViewMode.MODIFY
    private var paperId: Long = -1L
    private var storeId: Long = -1L
    private var adminName: String = ""
    private var partnerName: String = ""

    companion object {
        private const val ARG_CONTRACT_DATA = "contract_data"
        private const val ARG_ENTRY_TYPE = "entry_type"
        private const val ARG_PARTNER_ID = "partner_id"
        private const val ARG_PAPER_ID = "paper_id"
        private const val ARG_ADMIN_NAME = "adminName"
        private const val ARG_PARTNER_NAME = "partnerName"
        fun newInstance(
            entryType: ViewMode,
            partnerId: Long,
            paperId: Long,
            adminName: String = "",
            partnerName: String = ""
        ): ProposalModifyFragment {
            return ProposalModifyFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ENTRY_TYPE, entryType.name)
                    putLong(ARG_PARTNER_ID, partnerId)
                    putLong(ARG_PAPER_ID, paperId)
                    putString(ARG_ADMIN_NAME, adminName)
                    putString(ARG_PARTNER_NAME, partnerName)
                }
            }
        }
    }

    override fun initObserver() {
        initializeUI()
        setupObservers()

        if (contractData != null) {
            displayContractData(contractData!!)
        } else {
            loadProposalData()
        }
    }

    override fun initView() {
        // Arguments에서 데이터 추출
        arguments?.let {
            contractData = it.getSerializable(ARG_CONTRACT_DATA) as? PartnershipContractData
            entryType = ViewMode.valueOf(it.getString(ARG_ENTRY_TYPE, ViewMode.MODIFY.name))
            paperId = it.getLong(ARG_PAPER_ID, -1L)

            val userRole = authTokenLocalStore.getUserRole()
            if (userRole.equals("ADMIN", ignoreCase = true)) {
                adminName = authTokenLocalStore.getUserName() ?: ""
                partnerName = it.getString(ARG_PARTNER_NAME, "")
            } else {
                adminName = it.getString(ARG_ADMIN_NAME, "")
                partnerName = authTokenLocalStore.getUserName() ?: ""
            }

            if (adminName.isNotEmpty()) {
                partnershipViewModel.updateAdminName(adminName)
            }
            if (partnerName.isNotEmpty()) {
                partnershipViewModel.updatePartnerName(partnerName)
            }
        }

        initObserver()
    }

    private fun initializeUI() {
        setupUIByEntryType()

        // X 버튼 클릭
        binding.ivPartnershipContentCross.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack(null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        // 하단 버튼 클릭
        binding.llPartnershipCheck.setOnClickListener {
            handleBottomButtonClick()
        }
    }

    private fun setupUIByEntryType() {
        when (entryType) {
            ViewMode.MODIFY -> {
                binding.etFragmentServiceProposalAdmin.isEnabled = false
                binding.etFragmentServiceProposalPartner.isEnabled = false
                binding.llPartnershipCheck.setBackgroundResource(R.drawable.bg_chatting_proposal_box)
                binding.ivPartnershipModify.visibility = View.VISIBLE
                binding.tvPartnershipModify.text = "수정하기"
                binding.tvPartnershipModify.setTextColor(ContextCompat.getColor(requireContext(), R.color.assu_font_white))
            }
            ViewMode.QR_SAVE -> {
                binding.etFragmentServiceProposalAdmin.isEnabled = false
                binding.etFragmentServiceProposalPartner.isEnabled = false
                binding.llPartnershipCheck.setBackgroundResource(R.drawable.bg_chatting_call_box)
                binding.ivPartnershipModify.visibility = View.GONE
                binding.tvPartnershipModify.text = "QR저장"
                binding.tvPartnershipModify.setTextColor(ContextCompat.getColor(requireContext(), R.color.assu_font_main))
            }
        }
    }

    private fun displayContractData(data: PartnershipContractData) {
        // 상단 텍스트 설정
        binding.etFragmentServiceProposalPartner.setText(data.partnerName ?: "-")
        binding.etFragmentServiceProposalAdmin.setText(data.adminName ?: "-")
        binding.tvPartnershipContentStartDate.text = formatDate(data.periodStart ?: "")
        binding.tvPartnershipContentEndDate.text = formatDate(data.periodEnd ?: "")

        // RecyclerView 어댑터 설정
        proposalAdapter = ProposalModifyAdapter(data.options ?: emptyList())
        binding.rvPartnershipContentList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = proposalAdapter
        }

        saveDataToViewModel(data)
    }

    // API 응답을 PartnershipContractData로 변환하여 표시
    private fun displayProposalData(proposalData: ProposalPartnerDetailsModel) {
        // ProposalPartnerDetailsModel을 PartnershipContractData로 변환
        val contractData = convertToContractData(proposalData)
        this.storeId = proposalData.storeId ?: -1L

        if (contractData != null) {
            displayContractData(contractData)
        } else {
            showToast("데이터 변환 실패")
        }

        partnershipViewModel.partnershipStartDate.value = proposalData.periodStart
        partnershipViewModel.partnershipEndDate.value = proposalData.periodEnd
    }

    // ProposalPartnerDetailsModel을 PartnershipContractData로 변환
    private fun convertToContractData(data: ProposalPartnerDetailsModel): PartnershipContractData {
        val options = data.options?.mapNotNull { option ->
            when {
                option.optionType == OptionType.SERVICE && option.criterionType == CriterionType.HEADCOUNT -> {
                    PartnershipContractItem.Service.ByPeople(
                        minPeople = option.people ?: 0,
                        items = option.goods?.joinToString(", ") { it.goodsName ?: "" } ?: "",
                        category = option.category
                    )
                }

                option.optionType == OptionType.SERVICE && option.criterionType == CriterionType.PRICE -> {
                    PartnershipContractItem.Service.ByAmount(
                        minAmount = option.cost?.toInt() ?: 0,
                        items = option.goods?.joinToString(", ") { it.goodsName ?: "" } ?: "",
                        category = option.category
                    )
                }

                option.optionType == OptionType.DISCOUNT && option.criterionType == CriterionType.HEADCOUNT -> {
                    PartnershipContractItem.Discount.ByPeople(
                        minPeople = option.people ?: 0,
                        percent = option.discountRate?.toInt() ?: 0
                    )
                }

                option.optionType == OptionType.DISCOUNT && option.criterionType == CriterionType.PRICE -> {
                    PartnershipContractItem.Discount.ByAmount(
                        minAmount = option.cost?.toInt() ?: 0,
                        percent = option.discountRate?.toInt() ?: 0
                    )
                }

                else -> null
            }
        } ?: emptyList()

        return PartnershipContractData(
            partnerName = partnerName,
            adminName = adminName,
            options = options,
            periodStart = data.periodStart,
            periodEnd = data.periodEnd
        )
    }

    // ViewModel에 데이터 저장
    private fun saveDataToViewModel(data: PartnershipContractData) {
        partnershipViewModel.updatePartnerName(data.partnerName ?: "")
        partnershipViewModel.updateAdminName(data.adminName ?: "")
        partnershipViewModel.partnershipStartDate.value = data.periodStart ?: ""
        partnershipViewModel.partnershipEndDate.value = data.periodEnd ?: ""

        // options를 BenefitItem으로 변환하여 저장
        val benefitItems = data.options?.map { item ->
            when (item) {
                is PartnershipContractItem.Service.ByPeople -> {
                    BenefitItem(
                        id = System.currentTimeMillis().toString(),
                        optionType = com.example.assu_fe_app.data.dto.partnership.OptionType.SERVICE,
                        criterionType = com.example.assu_fe_app.data.dto.partnership.CriterionType.HEADCOUNT,
                        criterionValue = item.minPeople.toString(),
                        category = "",
                        goods = listOf(item.items),
                        discountRate = ""
                    )
                }
                is PartnershipContractItem.Service.ByAmount -> {
                    BenefitItem(
                        id = System.currentTimeMillis().toString(),
                        optionType = com.example.assu_fe_app.data.dto.partnership.OptionType.SERVICE,
                        criterionType = com.example.assu_fe_app.data.dto.partnership.CriterionType.PRICE,
                        criterionValue = item.minAmount.toString(),
                        category = "",
                        goods = listOf(item.items),
                        discountRate = ""
                    )
                }
                is PartnershipContractItem.Discount.ByPeople -> {
                    BenefitItem(
                        id = System.currentTimeMillis().toString(),
                        optionType = com.example.assu_fe_app.data.dto.partnership.OptionType.DISCOUNT,
                        criterionType = com.example.assu_fe_app.data.dto.partnership.CriterionType.HEADCOUNT,
                        criterionValue = item.minPeople.toString(),
                        category = "",
                        goods = emptyList(),
                        discountRate = item.percent.toString()
                    )
                }
                is PartnershipContractItem.Discount.ByAmount -> {
                    BenefitItem(
                        id = System.currentTimeMillis().toString(),
                        optionType = com.example.assu_fe_app.data.dto.partnership.OptionType.DISCOUNT,
                        criterionType = com.example.assu_fe_app.data.dto.partnership.CriterionType.PRICE,
                        criterionValue = item.minAmount.toString(),
                        category = "",
                        goods = emptyList(),
                        discountRate = item.percent.toString()
                    )
                }
            }
        } ?: emptyList()

        partnershipViewModel.updateBenefitItems(benefitItems)
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            partnershipViewModel.summaryText.collect { text ->
                if (text.isNotEmpty()) {
                    binding.tvPartnershipContentSign.text = text
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            partnershipViewModel.getPartnershipDetailUiState.collect { state ->
                Log.d("ProposalModifyFragment", "State collected: ${state::class.simpleName}")
                Log.d("ProposalModifyFragment", "State is Loading? ${state is PartnershipViewModel.PartnershipDetailUiState.Loading}")
                when (state) {
                    is PartnershipViewModel.PartnershipDetailUiState.Idle -> {
                        hideLoading()
                    }
                    is PartnershipViewModel.PartnershipDetailUiState.Loading -> {
                        Log.d("ProposalModifyFragment", "Matched: Loading - calling showLoading")
                        showLoading("로딩 중...")
                    }
                    is PartnershipViewModel.PartnershipDetailUiState.Success -> {
                        Log.d("ProposalModifyFragment", "Matched: Success")
                        hideLoading()
                        displayProposalData(state.data)
                    }
                    is PartnershipViewModel.PartnershipDetailUiState.Fail -> {
                        hideLoading()
                        showToast("제안서 조회 실패: ${state.message}")
                    }
                    is PartnershipViewModel.PartnershipDetailUiState.Error -> {
                        hideLoading()
                        showToast("오류가 발생했습니다: ${state.message}")
                    }
                }
            }
        }
    }

    private fun showLoading(message: String = "로딩 중...") {
        binding.loadingOverlay.visibility = View.VISIBLE
        binding.tvLoadingText.text = message
    }

    private fun hideLoading() {
        binding.loadingOverlay.visibility = android.view.View.GONE
    }

    private fun loadProposalData() {
        if (paperId > 0) {
            partnershipViewModel.getPartnershipDetail(paperId)
        }
    }

    private fun handleBottomButtonClick() {
        when (entryType) {
            ViewMode.MODIFY -> navigateToEditMode()
            ViewMode.QR_SAVE -> navigateToQrSave()
        }
    }

    // ✅ 수정 모드로 이동 - ServiceProposalWritingFragment로 이동
    private fun navigateToEditMode() {
        val fragment = ServiceProposalWritingFragment.Companion.newInstanceForEdit(
            partnerId = partnershipViewModel.partnerId,
            paperId = paperId,
            adminName = adminName,
            partnerName = partnerName,
            isEditMode = true // 수정 모드임을 표시
        )

        parentFragmentManager.beginTransaction()
            .replace(R.id.chatting_fragment_container, fragment)
            .addToBackStack(null)
            .commit()

        Log.d("ProposalModifyFragment", "Navigate to ServiceProposalWritingFragment for editing")
    }

    private fun navigateToQrSave() {
        if (storeId == -1L) {
            Toast.makeText(requireContext(), "가게 정보를 찾을 수 없어 QR 생성이 불가능합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val fragment = QrSaveFragment.newInstance(storeId)

        parentFragmentManager.beginTransaction()
            .replace(R.id.chatting_fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun formatDate(date: String): String {
        // 2025-05-05 형식을 2025 - 05 - 05로 변환
        return date.replace("-", " - ")
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}