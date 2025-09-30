package com.example.assu_fe_app.presentation.admin.dashboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentAdminSuggestionReportDialogBinding
import com.example.assu_fe_app.presentation.common.report.OnReviewReportConfirmedListener

class AdminSuggestionReportDialogFragment : DialogFragment() {
    private var _binding: FragmentAdminSuggestionReportDialogBinding? = null
    private val binding get() = _binding!!

    private var listener: OnReviewReportConfirmedListener? = null
    private var selectedOption: ReportOption? = null

    enum class ReportOption {
        INAPPROPRIATE,
        FALSE_INFO,
        PROMOTION
    }

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
        initializeRadioButtons()
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
        val dialogWidth = (width * 0.8369f).toInt()
        dialog?.window?.setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun initializeRadioButtons() {
        // 모든 옵션을 unselected 상태로 초기화
        setOptionState(binding.rbInappropriate, isSelected = false)
        setOptionState(binding.rbFalseInfo, isSelected = false)
        setOptionState(binding.rbPromotion, isSelected = false)
    }

    private fun setupClickListeners() {
        binding.rbInappropriate.setOnClickListener {
            selectOption(ReportOption.INAPPROPRIATE)
        }

        binding.rbFalseInfo.setOnClickListener {
            selectOption(ReportOption.FALSE_INFO)
        }

        binding.rbPromotion.setOnClickListener {
            selectOption(ReportOption.PROMOTION)
        }

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

        binding.ivCloseButton.setOnClickListener {
            dismiss()
        }
    }

    private fun selectOption(option: ReportOption) {
        selectedOption = option

        // 모든 옵션을 unselected로 설정
        setOptionState(binding.rbInappropriate, isSelected = false)
        setOptionState(binding.rbFalseInfo, isSelected = false)
        setOptionState(binding.rbPromotion, isSelected = false)

        // 선택된 옵션만 selected로 설정
        when (option) {
            ReportOption.INAPPROPRIATE -> setOptionState(binding.rbInappropriate, isSelected = true)
            ReportOption.FALSE_INFO -> setOptionState(binding.rbFalseInfo, isSelected = true)
            ReportOption.PROMOTION -> setOptionState(binding.rbPromotion, isSelected = true)
        }
    }

    private fun setOptionState(layout: View, isSelected: Boolean) {
        // LinearLayout 내부의 첫 번째 자식(ImageView)과 두 번째 자식(TextView) 찾기
        val viewGroup = layout as? ViewGroup ?: return
        val imageView = viewGroup.getChildAt(0) as? ImageView
        val textView = viewGroup.getChildAt(1) as? TextView

        if (isSelected) {
            imageView?.setImageResource(R.drawable.btn_radio_selected)
            textView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.assu_font_main))
        } else {
            imageView?.setImageResource(R.drawable.btn_radio_unselected)
            textView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.assu_font_sub))
        }
    }

    private fun getSelectedReportReason(): String {
        return when (selectedOption) {
            ReportOption.INAPPROPRIATE -> "INAPPROPRIATE_CONTENT"
            ReportOption.FALSE_INFO -> "FALSE_INFORMATION"
            ReportOption.PROMOTION -> "SPAM_PROMOTION"
            null -> "OTHER"
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