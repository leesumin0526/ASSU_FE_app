package com.example.assu_fe_app.presentation.common.chatting.proposal

import android.content.res.ColorStateList
import android.text.Editable
import android.text.TextWatcher
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentServiceProposalTermWritingBinding
import com.example.assu_fe_app.databinding.ItemServiceProposalSetBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.chatting.ChattingSentProposalFragment


class ServiceProposalTermWritingFragment :
    BaseFragment<FragmentServiceProposalTermWritingBinding>(R.layout.fragment_service_proposal_term_writing) {
    override fun initObserver() {}

    override fun initView() {
        binding.etFragmentServiceProposalSign4.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val hasText = !s.isNullOrBlank()
                val newColor = if (hasText) {
                    ContextCompat.getColor(requireContext(), R.color.assu_main)
                } else {
                    ContextCompat.getColor(requireContext(), R.color.assu_sub) // 기본색으로
                }

                binding.btnCompleted.backgroundTintList = ColorStateList.valueOf(newColor)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.btnCompleted.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.chatting_fragment_container, ChattingSentProposalFragment())
                .addToBackStack(null) // 뒤로가기 가능하게
                .commit()
        }

        binding.ivFragmentServiceProposalBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }
}