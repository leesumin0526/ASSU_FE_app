package com.assu.app.presentation.common.chatting

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import com.assu.app.presentation.common.contract.ProposalModifyFragment
import com.assu.app.R
import com.assu.app.databinding.FragmentChattingSentProposalBinding
import com.assu.app.presentation.base.BaseFragment
import com.assu.app.presentation.common.contract.ViewMode
import com.assu.app.ui.partnership.PartnershipViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChattingSentProposalFragment : BaseFragment<FragmentChattingSentProposalBinding>(R.layout.fragment_chatting_sent_proposal) {

    private val viewModel: PartnershipViewModel by activityViewModels()
    private var isAgreementComplete: Boolean = false
    private var partnershipStatus: String? = null

    companion object {
        fun newInstance(
            paperId: Long,
            partnerId: Long,
            partnershipStatus: String? = null,
            isAgreementComplete: Boolean,
            adminName: String? = null,
            partnerName: String? = null
        ): ChattingSentProposalFragment {
            return ChattingSentProposalFragment().apply {
                arguments = Bundle().apply {
                    putLong("paperId", paperId)
                    putLong("partnerId", partnerId)
                    partnershipStatus?.let { putString("partnershipStatus", it) }
                    putBoolean("isAgreementComplete", isAgreementComplete)
                    adminName?.let { putString("adminName", it) }
                    partnerName?.let { putString("partnerName", it) }
                }
            }
        }
    }

    override fun initView() {
        // Arguments에서 데이터 추출
        extractArguments()

        // 체결 완료 여부에 따라 UI 변경
        updateUIByStatus()

        binding.ivCross.setOnClickListener {
            popToRootFragment("x 버튼")
        }

        binding.bgImage.setOnClickListener {
            handleButtonClick()
        }

        // ✅ 제안서 확인하기 버튼 클릭 시 ProposalModifyFragment로 이동
        binding.btnText.setOnClickListener {
            handleButtonClick()
        }
    }

    private fun extractArguments() {
        arguments?.let { bundle ->
            isAgreementComplete = bundle.getBoolean("isAgreementComplete", false)
            partnershipStatus = bundle.getString("partnershipStatus")

            // ViewModel에 데이터 설정
            bundle.getString("adminName")?.let {
                viewModel.updateAdminName(it)
            }
            bundle.getString("partnerName")?.let {
                viewModel.updatePartnerName(it)
            }

            viewModel.paperId = bundle.getLong("paperId", -1L)
            viewModel.partnerId = bundle.getLong("partnerId", -1L)
        }
    }

    private fun updateUIByStatus() {
        if (isAgreementComplete) {
            // 체결 완료 상태
            binding.tvSendingSuccess.text = "체결 완료"
            binding.tvFinishReviewThanks.text = "제휴가 무사히 체결되었어요!"
            binding.btnText.text = "계약서 확인하기"
        } else {
            // 기본 상태
            binding.tvSendingSuccess.text = "전송 완료"
            binding.tvFinishReviewThanks.text = "소중한 제안서가 전송 완료됐어요"
            binding.btnText.text = "제안서 확인하기"
        }
    }

    private fun handleButtonClick() {
        if (isAgreementComplete) {
            navigateToContractView()
        } else {
            navigateToProposalModify()
        }
    }

    private fun navigateToContractView() {
        val fragment = ProposalModifyFragment.newInstance(
            entryType = ViewMode.QR_SAVE,
            partnerId = viewModel.partnerId,
            paperId = viewModel.paperId,
            adminName = viewModel.adminName.value,
            partnerName = viewModel.partnerName.value
        )

        parentFragmentManager.beginTransaction()
            .replace(R.id.chatting_fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToProposalModify() {
        val fragment = ProposalModifyFragment.newInstance(
            entryType = ViewMode.MODIFY,
            partnerId = viewModel.partnerId,
            paperId = viewModel.paperId,
            adminName = viewModel.adminName.value,
            partnerName = viewModel.partnerName.value
        )

        parentFragmentManager.beginTransaction()
            .replace(R.id.chatting_fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun popToRootFragment(reason: String) {
        // 결과 먼저 보내기
        val result = Bundle().apply {
            putString("reason", reason)
        }
        parentFragmentManager.setFragmentResult("return_reason", result)

        parentFragmentManager.beginTransaction()
            .remove(this)
            .commit()
    }

    override fun initObserver() {}
}
