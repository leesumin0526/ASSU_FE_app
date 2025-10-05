package com.assu.app.presentation.admin.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.assu.app.databinding.FragmentAdminSuggestionReportCompleteDialogBinding

class AdminSuggestionReportCompleteDialogFragment : DialogFragment() {

    private var _binding: FragmentAdminSuggestionReportCompleteDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminSuggestionReportCompleteDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val position = arguments?.getInt("position") ?: -1
        val isStudentReport = arguments?.getBoolean("isStudentReport", false) ?: false

        // 텍스트 변경
        if (isStudentReport) {
            binding.tvCompleteTitle.text = "제휴건의 사용자의 신고가\n완료되었습니다!"
            binding.tvCompleteDescription.text = "신고 직후 해당 사용자가 작성한 모든 제휴건의글은 비공개 처리되며, 해당 사실이 작성자에게 고지되지 않습니다."
        } else {
            binding.tvCompleteTitle.text = "제휴건의글의 신고가\n완료되었습니다!"
            binding.tvCompleteDescription.text = "신고 직후 해당 제휴건의글은 비공개 처리되며, 해당 사실이 작성자에게 고지되지 않습니다."
        }

        binding.btnConfirm.setOnClickListener {
            (activity as? AdminDashboardSuggestionsActivity)?.onReportCompleteConfirmed(position)
            dismiss()
        }

        binding.ivCloseButton.setOnClickListener {
            (activity as? AdminDashboardSuggestionsActivity)?.onReportCompleteConfirmed(position)
            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        val displayMetrics = resources.displayMetrics
        val width = displayMetrics.widthPixels
        val dialogWidth = (width * 0.8396f).toInt()
        dialog?.window?.setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(position: Int, isStudentReport: Boolean = false): AdminSuggestionReportCompleteDialogFragment {
            val fragment = AdminSuggestionReportCompleteDialogFragment()
            val args = Bundle().apply {
                putInt("position", position)
                putBoolean("isStudentReport", isStudentReport)
            }
            fragment.arguments = args
            return fragment
        }
    }
}