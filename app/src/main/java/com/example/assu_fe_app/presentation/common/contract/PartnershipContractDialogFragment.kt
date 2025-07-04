package com.example.assu_fe_app.presentation.common.contract

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assu_fe_app.data.dto.partner_admin.home.PartnershipContractItem
import com.example.assu_fe_app.databinding.FragmentPartnershipContentBinding
import com.example.assu_fe_app.presentation.common.contract.adapter.PartnershipContractAdapter

class PartnershipContractDialogFragment(
    private val partnershipContractItems: List<PartnershipContractItem>
) : DialogFragment( ) {

    private var _binding: FragmentPartnershipContentBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: PartnershipContractAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPartnershipContentBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onStart() {
        super.onStart()
        val width  = (resources.displayMetrics.widthPixels  * 0.9).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.8).toInt()  // 화면 높이의 80%
        dialog?.window?.setLayout(width, height)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PartnershipContractAdapter(partnershipContractItems)

        binding.rvPartnershipContentList.apply {
            adapter = PartnershipContractAdapter(partnershipContractItems)
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.ivPartnershipContentCross.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}