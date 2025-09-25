package com.example.assu_fe_app.presentation.common.chatting.proposal

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentServiceProposalWritingBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.chatting.proposal.adapter.ServiceProposalAdapter

//엥
class ServiceProposalWritingFragment
    : BaseFragment<FragmentServiceProposalWritingBinding>(R.layout.fragment_service_proposal_writing) {

    val adapter = ServiceProposalAdapter {
        onItemOptionSelected()
        checkAllFieldsFilled()
    }

    override fun initView() {

        parentFragmentManager.setFragmentResultListener("result", this) { _, bundle ->
            val resultData = bundle.getString("selectedPlace")
            Log.d("SignupInfoFragment", "받은 데이터: $resultData")

            binding.tvFragmentServiceProposalPartner.text = resultData
        }
        binding.rvFragmentServiceProposalItemSet.adapter = adapter
        binding.rvFragmentServiceProposalItemSet.layoutManager = LinearLayoutManager(requireContext())

        if (adapter.getItems().isEmpty()) {
            adapter.addItem()
        }

        binding.tvAddProposalItem.setOnClickListener {
            adapter.addItem()
            checkAllFieldsFilled()
            Log.d("addItem", "writingFragment2")
        }

        binding.btnCompleted.setOnClickListener {
//            parentFragmentManager.beginTransaction()
//                .replace(R.id.chatting_fragment_container, ServiceProposalTermWritingFragment())
//                .addToBackStack(null) // 뒤로가기 가능하게
//                .commit()


            findNavController().navigate(
                R.id.action_serviceProposalWritingFragment_to_serviceProposalTermWritingFragment)
        }

        binding.ivFragmentServiceProposalBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.tvFragmentServiceProposalPartner.setOnClickListener {
            val bundle = Bundle().apply{
                putString("type", "passive")
            }

            findNavController().navigate(
                R.id.action_serviceProposalWritingFragment_to_locationSearchFragment, bundle)
        }


        setUpFragmentEditTextWatchers()
        checkAllFieldsFilled()
    }

    private fun onItemOptionSelected() {
        binding.tvAddProposalItem.visibility = View.VISIBLE
    }

    override fun initObserver() {}

    private fun setUpFragmentEditTextWatchers() {
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = checkAllFieldsFilled()
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }


        binding.etFragmentServiceProposalAdmin.addTextChangedListener(watcher)
    }

    private fun checkAllFieldsFilled() {
        val partnerFilled = binding.tvFragmentServiceProposalPartner.text?.isNotBlank() == true
        val adminFilled = binding.etFragmentServiceProposalAdmin.text?.isNotBlank() == true
        val itemFieldsFilled = adapter.getItems().all { item ->
            item.contents.all{it.isNotBlank()}
        }

        val allFilled = partnerFilled && adminFilled && itemFieldsFilled

        val colorRes = if (allFilled) R.color.assu_main else R.color.assu_sub
        binding.btnCompleted.backgroundTintList = ContextCompat.getColorStateList(requireContext(), colorRes)

        binding.btnCompleted.isEnabled = allFilled
    }
}
