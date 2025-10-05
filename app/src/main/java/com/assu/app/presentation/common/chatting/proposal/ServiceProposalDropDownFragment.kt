package com.assu.app.presentation.common.chatting.proposal

import com.assu.app.R
import com.assu.app.databinding.FragmentProposalDropdownBinding
import com.assu.app.presentation.base.BaseFragment

class ServiceProposalDropDownFragment(
) : BaseFragment<FragmentProposalDropdownBinding>(R.layout.fragment_proposal_dropdown) {

    private var onOptionSelectedListener: ((String) -> Unit)? = null
    fun setOnOptionSelectedListener(listener: (String) -> Unit) {
        this.onOptionSelectedListener = listener
    }

    override fun initView() {
        binding.tvProposalOption1.setOnClickListener {
            onOptionSelectedListener?.invoke("서비스 제공")
        }
        binding.tvProposalOption2.setOnClickListener {
            onOptionSelectedListener?.invoke("할인 혜택")
        }
    }

    override fun initObserver() {}

}