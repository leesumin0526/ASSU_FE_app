package com.example.assu_fe_app.presentation.partner.dashboard.review

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
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentReviewReportDialogBinding
import com.example.assu_fe_app.presentation.common.report.OnReviewReportConfirmedListener

class ReviewReportDialogFragment : DialogFragment() {
    private var _binding: FragmentReviewReportDialogBinding? = null
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
        initializeRadioButtons()
        setupClickListeners()
        return binding.root
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

    private fun getSelectedReportReason(): String {
        return when (selectedOption) {
            ReportOption.INAPPROPRIATE -> "INAPPROPRIATE_CONTENT"
            ReportOption.FALSE_INFO -> "FALSE_INFORMATION"
            ReportOption.PROMOTION -> "SPAM_PROMOTION"
            null -> "OTHER"
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

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    companion object {
        fun newInstance(position: Int): ReviewReportDialogFragment {
            val fragment = ReviewReportDialogFragment()
            val args = Bundle().apply {
                putInt("position", position)
            }
            fragment.arguments = args
            return fragment
        }
    }
}