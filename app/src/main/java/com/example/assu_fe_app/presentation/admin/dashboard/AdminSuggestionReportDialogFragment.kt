package com.example.assu_fe_app.presentation.admin.dashboard


import android.content.Context
import android.os.Bundle
import com.example.assu_fe_app.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.assu_fe_app.databinding.FragmentAdminSuggestionReportDialogBinding
import com.example.assu_fe_app.presentation.partner.dashboard.review.OnReviewReportConfirmedListener

class AdminSuggestionReportDialogFragment : DialogFragment(){
    private var _binding: FragmentAdminSuggestionReportDialogBinding? = null
    private val binding get() = _binding!!

    // 1. 리스너를 담을 변수 추가
    private var listener: OnReviewReportConfirmedListener? = null

    // 2. onAttach 콜백에서 리스너 설정
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is OnReviewReportConfirmedListener) {
            listener = parentFragment as OnReviewReportConfirmedListener
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

    // onResume은 기존 코드와 동일하게 유지
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

            // 3. activity 대신 설정된 listener를 호출하도록 변경
            listener?.onReviewReportConfirmed(position, selectedReportReason)

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // onDetach에서 리스너를 null로 만들어 메모리 누수 방지
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