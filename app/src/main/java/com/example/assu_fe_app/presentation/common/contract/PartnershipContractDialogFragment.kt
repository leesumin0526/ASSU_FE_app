package com.example.assu_fe_app.presentation.common.contract

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assu_fe_app.data.dto.partnership.PartnershipContractData
import com.example.assu_fe_app.databinding.FragmentPartnershipContentBinding
import com.example.assu_fe_app.presentation.common.contract.adapter.PartnershipContractAdapter

// 계약서
class PartnershipContractDialogFragment() : DialogFragment( ) {

    private var _binding: FragmentPartnershipContentBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: PartnershipContractAdapter
    private var contractData: PartnershipContractData? = null

    var onDismissListener: (() -> Unit)? = null

    companion object {
        private const val ARG_CONTRACT = "contract"

        fun newInstance(data: PartnershipContractData): PartnershipContractDialogFragment {
            return PartnershipContractDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_CONTRACT, data) // Parcelable 아니면 Serializable 사용
                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.invoke()
    }


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

        contractData?.let { data ->
            // 상단 텍스트
            binding.tvPartnershipContentPartner.text = data.partnerName ?: "-"
            binding.tvPartnershipContentAdmin.text = data.adminName ?: "-"
            binding.tvPartnershipContentStartDate.text = data.periodStart ?: ""
            binding.tvPartnershipContentEndDate.text = data.periodEnd ?: ""

            val summaryText = buildString {
                append("위와 같이 ")
                append(data.adminName ?: "-")
                append("와의\n 제휴를 제안합니다.\n\n")
                append(data.periodStart?: "")
                append("\n")
                append(data.partnerName + "(인)")
            }
            binding.tvPartnershipContentSummary.text = summaryText


            // 옵션 리스트
            adapter = PartnershipContractAdapter(data.options ?: emptyList())
            binding.rvPartnershipContentList.apply {
                adapter = this@PartnershipContractDialogFragment.adapter
                layoutManager = LinearLayoutManager(requireContext())
            }
        }

        binding.ivPartnershipContentCross.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contractData = arguments?.getSerializable( ARG_CONTRACT) as? PartnershipContractData
    }
}