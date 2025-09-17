package com.example.assu_fe_app.presentation.common.chatting

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentChattingSentProposalBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChattingSentProposalFragment : BaseFragment<FragmentChattingSentProposalBinding>(R.layout.fragment_chatting_sent_proposal) {
    override fun initView() {
        binding.ivCross.setOnClickListener {
            popToRootFragment("x 버튼")
        }

        binding.bgImage.setOnClickListener {
            popToRootFragment("제휴계약서 모달 보기 위함")
        }

        binding.btnText.setOnClickListener {
            popToRootFragment("제휴계약서 모달 보기 위함")
        }
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

    override fun initObserver() {
    }
}
