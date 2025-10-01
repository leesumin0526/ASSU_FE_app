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
    private var isStudentReport: Boolean = false

    enum class ReportOption(val apiValue: String) {
        INAPPROPRIATE("INAPPROPRIATE_CONTENT"),
        FALSE_INFO("FALSE_INFORMATION"),
        PROMOTION("SPAM")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
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

        // arguments에서 isStudentReport 가져오기
        isStudentReport = arguments?.getBoolean("isStudentReport", false) ?: false

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // 다이얼로그 제목 변경
        if (isStudentReport) {
            binding.tvQuestionRealReportReview.text = "사용자를 신고하는\n사유를 선택해주세요"
        } else {
            binding.tvQuestionRealReportReview.text = "제휴건의글을 신고하는\n사유를 선택해주세요"
        }

        // 라디오 버튼 텍스트 변경
        updateRadioButtonTexts(isStudentReport)

        initializeRadioButtons()
        setupClickListeners()
    }

    private fun updateRadioButtonTexts(isStudentReport: Boolean) {
        val inappropriateText: TextView = binding.rbInappropriate.getChildAt(1) as TextView
        val falseInfoText: TextView = binding.rbFalseInfo.getChildAt(1) as TextView
        val promotionText: TextView = binding.rbPromotion.getChildAt(1) as TextView

        if (isStudentReport) {
            // 작성자 신고 텍스트
            inappropriateText.text = "부적절한 내용 및 욕설이 포함된 글을 작성했어요"
            falseInfoText.text = "허위사실 / 거짓이 포함된 글을 작성했어요"
            promotionText.text = "홍보/광고를 위한 건의글을 작성했어요"
        } else {
            // 콘텐츠 신고 텍스트 (기본값 유지)
            inappropriateText.text = "부적절한 내용 및 욕설이 포함된 건의글이에요"
            falseInfoText.text = "허위사실 / 거짓이 포함된 건의글에요"
            promotionText.text = "홍보/광고를 위한 건의글이에요"
        }
    }

    override fun onResume() {
        super.onResume()
        val displayMetrics = resources.displayMetrics
        val width = displayMetrics.widthPixels
        val dialogWidth = (width * 0.8369f).toInt()
        dialog?.window?.setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun initializeRadioButtons() {
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

            listener?.onReviewReportConfirmed(position, selectedReportReason)
            dismiss()
        }

        binding.ivCloseButton.setOnClickListener {
            dismiss()
        }
    }

    private fun selectOption(option: ReportOption) {
        selectedOption = option

        setOptionState(binding.rbInappropriate, isSelected = false)
        setOptionState(binding.rbFalseInfo, isSelected = false)
        setOptionState(binding.rbPromotion, isSelected = false)

        when (option) {
            ReportOption.INAPPROPRIATE -> setOptionState(binding.rbInappropriate, isSelected = true)
            ReportOption.FALSE_INFO -> setOptionState(binding.rbFalseInfo, isSelected = true)
            ReportOption.PROMOTION -> setOptionState(binding.rbPromotion, isSelected = true)
        }
    }

    private fun setOptionState(layout: View, isSelected: Boolean) {
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
        val baseType = selectedOption?.apiValue ?: "OTHER"

        // isStudentReport에 따라 접두사 결정
        val prefix = if (isStudentReport) {
            "STUDENT_USER_"
        } else {
            "SUGGESTION_"
        }

        return prefix + baseType
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
        fun newInstance(position: Int, isStudentReport: Boolean = false): AdminSuggestionReportDialogFragment {
            val fragment = AdminSuggestionReportDialogFragment()
            val args = Bundle().apply {
                putInt("position", position)
                putBoolean("isStudentReport", isStudentReport)
            }
            fragment.arguments = args
            return fragment
        }
    }
}