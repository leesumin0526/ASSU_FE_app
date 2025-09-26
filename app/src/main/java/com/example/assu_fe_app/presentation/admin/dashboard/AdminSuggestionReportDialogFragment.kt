package com.example.assu_fe_app.presentation.admin.dashboard

import android.content.Context
import android.os.Bundle
import com.example.assu_fe_app.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.assu_fe_app.databinding.FragmentAdminSuggestionReportDialogBinding
import com.example.assu_fe_app.presentation.common.report.OnReviewReportConfirmedListener

class AdminSuggestionReportDialogFragment : DialogFragment() {
    private var _binding: FragmentAdminSuggestionReportDialogBinding? = null
    private val binding get() = _binding!!

    private var listener: OnReviewReportConfirmedListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Activity가 리스너를 구현했는지 확인
        if (context is OnReviewReportConfirmedListener) {
            listener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminSuggestionReportDialogBinding.inflate(inflater, container, false)
        setupClickListeners()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onResume() {
        super.onResume()
        val displayMetrics = resources.displayMetrics
        val width = displayMetrics.widthPixels
        val dialogWidth = (width * 0.9f).toInt()
        dialog?.window?.setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun setupClickListeners() {
        binding.btnReportCancel.setOnClickListener {
            dismiss()
        }

        binding.btnReportConfirm.setOnClickListener {
            val position = arguments?.getInt("position") ?: return@setOnClickListener
            val selectedReportReason = getSelectedReportReason()

            // Activity의 리스너 호출
            listener?.onReviewReportConfirmed(position, selectedReportReason)
            dismiss() // 이 다이얼로그는 닫고, 완료 다이얼로그는 Activity에서 띄움
        }
    }

    private fun getSelectedReportReason(): String {
        return when (binding.radioGroup.checkedRadioButtonId) {
            R.id.rb_inappropriate -> "INAPPROPRIATE_CONTENT"
            R.id.rb_false_info -> "FALSE_INFORMATION"
            R.id.rb_promotion -> "SPAM_PROMOTION"
            else -> "OTHER"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    companion object {
        fun newInstance(position: Int): AdminSuggestionReportDialogFragment {
            val fragment = AdminSuggestionReportDialogFragment()
            val args = Bundle().apply {
                putInt("position", position)
            }
            fragment.arguments = args
            return fragment
        }
    }
}