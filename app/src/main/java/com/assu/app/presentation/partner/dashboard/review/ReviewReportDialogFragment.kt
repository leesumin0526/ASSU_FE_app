package com.assu.app.presentation.partner.dashboard.review

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.assu.app.R
import com.assu.app.databinding.FragmentReviewReportDialogBinding
import com.assu.app.presentation.common.report.OnReviewReportConfirmedListener

class ReviewReportDialogFragment : DialogFragment() {
    private var _binding: FragmentReviewReportDialogBinding? = null
    private val binding get() = _binding!!

    private var listener: OnReviewReportConfirmedListener? = null
    private var selectedOption: ReportOption? = null
    private var isStudentReport: Boolean = false  // 추가


    enum class ReportOption(val apiValue: String) {  // apiValue 추가
        INAPPROPRIATE("INAPPROPRIATE_CONTENT"),
        FALSE_INFO("FALSE_INFORMATION"),
        PROMOTION("SPAM")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // Navigation Component를 사용하는 경우, NavHostFragment의 childFragmentManager에서 현재 활성 Fragment를 찾음
        val navHostFragment = parentFragment
        if (navHostFragment != null) {
            val currentFragment = navHostFragment.childFragmentManager.fragments.firstOrNull()

            if (currentFragment is OnReviewReportConfirmedListener) {
                listener = currentFragment
            }
        }

        // 여전히 리스너가 없다면 다른 방법들 시도
        if (listener == null) {
            // 부모 프래그먼트 확인
            if (parentFragment is OnReviewReportConfirmedListener) {
                listener = parentFragment as OnReviewReportConfirmedListener
                Log.d("ReviewReportDialog", "Listener set to parentFragment")
            }
            // Activity 확인
            else if (context is OnReviewReportConfirmedListener) {
                listener = context
            } else {
                Log.e("ReviewReportDialog", "No listener found! Neither currentFragment, parentFragment nor context implements OnReviewReportConfirmedListener")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewReportDialogBinding.inflate(inflater, container, false)

        // arguments에서 isStudentReport 가져오기
        isStudentReport = arguments?.getBoolean("isStudentReport", false) ?: false

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // 다이얼로그 제목 변경
        if (isStudentReport) {
            binding.tvQuestionRealReportReview.text = "작성자를 신고하는\n사유를 선택해주세요"
        } else {
            binding.tvQuestionRealReportReview.text = "고객리뷰를 신고하는\n사유를 선택해주세요"
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
            // 콘텐츠 신고 텍스트
            inappropriateText.text = "부적절한 내용 및 욕설이 포함된 리뷰예요"
            falseInfoText.text = "허위사실 / 거짓이 포함된 리뷰예요"
            promotionText.text = "홍보/광고를 위한 리뷰예요"
        }
    }


    override fun onResume() {
        super.onResume()
        val displayMetrics = resources.displayMetrics
        val width = displayMetrics.widthPixels
        val dialogWidth = (width * 0.9f).toInt()
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

        binding.ivCloseButton.setOnClickListener {
            dismiss()
        }

        binding.btnReportConfirm.setOnClickListener {
            val position = arguments?.getInt("position") ?: return@setOnClickListener
            val selectedReportReason = getSelectedReportReason()
            listener?.let { l ->
                Log.d("ReviewReportDialog", "Calling onReviewReportConfirmed")
                l.onReviewReportConfirmed(position, selectedReportReason)
                dismiss()
            } ?: Log.e("ReviewReportDialog", "Listener is null!")
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



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun getSelectedReportReason(): String {
        val baseType = selectedOption?.apiValue ?: "OTHER"

        // isStudentReport에 따라 접두사 결정
        val prefix = if (isStudentReport) {
            "STUDENT_USER_"
        } else {
            "REVIEW_"
        }

        return prefix + baseType
    }

    companion object {
        fun newInstance(position: Int, isStudentReport: Boolean = false): ReviewReportDialogFragment {
            val fragment = ReviewReportDialogFragment()
            val args = Bundle().apply {
                putInt("position", position)
                putBoolean("isStudentReport", isStudentReport)
            }
            fragment.arguments = args
            return fragment
        }
    }
}