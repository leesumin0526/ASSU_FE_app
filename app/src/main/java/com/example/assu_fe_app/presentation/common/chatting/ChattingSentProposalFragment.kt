package com.example.assu_fe_app.presentation.common.chatting

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.ProposalModifyFragment
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentChattingSentProposalBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.contract.ViewMode
import com.example.assu_fe_app.ui.partnership.PartnershipViewModel
import com.example.assu_fe_app.ui.partnership.WritePartnershipUiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChattingSentProposalFragment : BaseFragment<FragmentChattingSentProposalBinding>(R.layout.fragment_chatting_sent_proposal) {

    private val viewModel: PartnershipViewModel by activityViewModels()

    override fun initView() {
        binding.ivCross.setOnClickListener {
            popToRootFragment("x 버튼")
        }

        binding.bgImage.setOnClickListener {
            navigateToProposalModify()
        }

        // ✅ 제안서 확인하기 버튼 클릭 시 ProposalModifyFragment로 이동
        binding.btnText.setOnClickListener {
            navigateToProposalModify()
        }
    }

    // ✅ ProposalModifyFragment로 이동하는 함수
    private fun navigateToProposalModify() {
        // ViewModel에서 현재 제안서 데이터를 가져와서 전달
        val partnerId = viewModel.partnerId
        val paperId = viewModel.paperId

        val fragment = ProposalModifyFragment.newInstance(
            entryType = ViewMode.MODIFY,
            partnerId = partnerId,
            paperId = paperId
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

        // 백스택 초기화
        requireActivity()
            .supportFragmentManager
            .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    override fun initObserver() {}
}
