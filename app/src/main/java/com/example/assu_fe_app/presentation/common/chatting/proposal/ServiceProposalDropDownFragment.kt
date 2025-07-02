package com.example.assu_fe_app.presentation.common.chatting.proposal

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentProposalDropdownBinding
import com.example.assu_fe_app.databinding.ItemServiceProposalSetBinding
import com.example.assu_fe_app.presentation.base.BaseFragment

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