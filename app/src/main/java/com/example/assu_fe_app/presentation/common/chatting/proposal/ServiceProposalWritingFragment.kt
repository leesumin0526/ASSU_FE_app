package com.example.assu_fe_app.presentation.common.chatting.proposal

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentServiceProposalWritingBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.chatting.proposal.adapter.ServiceProposalAdapter

class ServiceProposalWritingFragment
    : BaseFragment<FragmentServiceProposalWritingBinding>(R.layout.fragment_service_proposal_writing) {

    val adapter = ServiceProposalAdapter {
        binding.ivFragmentServiceProposalAddBtn.visibility = View.VISIBLE
        checkAllFieldsFilled()
    }

    override fun initView() {
        binding.clFragmentServiceProposalItemSet.adapter = adapter
        binding.clFragmentServiceProposalItemSet.layoutManager = LinearLayoutManager(requireContext())

        adapter.addItem()
        Log.d("addItem", "writingFragment1")

        binding.ivFragmentServiceProposalAddBtn.setOnClickListener {
            adapter.addItem()
            Log.d("addItem", "writingFragment2")
        }

        binding.btnCompleted.setOnClickListener {
            findNavController().navigate(R.id.action_serviceProposalWritingFragment_to_serviceProposalTermWritingFragment)
        }

        setUpFragmentEditTextWatchers()
        checkAllFieldsFilled()
    }

    private fun onItemOptionSelected() {
        binding.ivFragmentServiceProposalAddBtn.visibility = View.VISIBLE
    }

    override fun initObserver() {}

    private fun setUpFragmentEditTextWatchers() {
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = checkAllFieldsFilled()
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        binding.etFragmentServiceProposalPartner.addTextChangedListener(watcher)
        binding.etFragmentServiceProposalAdmin.addTextChangedListener(watcher)
    }

    private fun checkAllFieldsFilled() {
        val partnerFilled = binding.etFragmentServiceProposalPartner.text?.isNotBlank() == true
        val adminFilled = binding.etFragmentServiceProposalAdmin.text?.isNotBlank() == true
        val itemFieldsFilled = adapter.getItems().all {
            it.num.isNotBlank() && it.content.isNotBlank()
        }

        val colorRes = if (partnerFilled && adminFilled && itemFieldsFilled) R.color.assu_main else R.color.assu_sub
        binding.btnCompleted.backgroundTintList = ContextCompat.getColorStateList(requireContext(), colorRes)
    }
}
