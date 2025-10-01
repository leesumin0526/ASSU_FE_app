package com.example.assu_fe_app.presentation.partner.dashboard.review

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.assu_fe_app.R
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.databinding.FragmentReportTargetDialogFragmentBinding
import com.example.assu_fe_app.presentation.common.report.OnReportTargetSelectedListener

class ReportTargetDialogFragment : DialogFragment() {

    private var _binding: FragmentReportTargetDialogFragmentBinding? = null
    private val binding get() = _binding!!

    private var listener: OnReportTargetSelectedListener? = null
    private var selectedTarget: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportTargetDialogFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)


        // Navigation Component를 사용하는 경우, NavHostFragment의 childFragmentManager에서 현재 활성 Fragment를 찾음
        val navHostFragment = parentFragment
        if (navHostFragment != null) {
            val currentFragment = navHostFragment.childFragmentManager.fragments.firstOrNull()

            if (currentFragment is OnReportTargetSelectedListener) {
                listener = currentFragment
            }
        }

        // 여전히 리스너가 없다면 다른 방법들 시도
        if (listener == null) {
            // 부모 프래그먼트 확인
            if (parentFragment is OnReportTargetSelectedListener) {
                listener = parentFragment as OnReportTargetSelectedListener
            }
            // Activity 확인
            else if (context is OnReportTargetSelectedListener) {
                listener = context
            } else {
                Log.e("ReportTargetDialog", "No listener found! Neither currentFragment, parentFragment nor context implements OnReportTargetSelectedListener")
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // X 버튼 → 닫기
        binding.btnReviewReportTargetCross.setOnClickListener {
            dismiss()
        }

        // 선택 항목 클릭
        val optionViews = listOf(binding.tvReviewReportReviewWriter, binding.tvReviewReportReview)

        // 초기화
        optionViews.forEach {
            it.isSelected = false
            it.setTextColor(ContextCompat.getColor(requireContext(), R.color.assu_font_sub))
        }

        // 완료 버튼 초기 상태 비활성화
        binding.btnReviewReportTargetConfirm.isEnabled = false

        // 클릭 리스너 등록
        optionViews.forEach { textView ->
            textView.setOnClickListener { clicked ->
                // 전체 해제
                optionViews.forEach { other ->
                    other.isSelected = false
                    other.setTextColor(ContextCompat.getColor(requireContext(), R.color.assu_font_sub))
                }

                // 현재 선택
                clicked.isSelected = true
                (clicked as TextView).setTextColor(ContextCompat.getColor(requireContext(), R.color.assu_main))

                // 선택된 값 저장
                selectedTarget = clicked.text.toString()

                // 완료 버튼 활성화
                binding.btnReviewReportTargetConfirm.isEnabled = true
            }
        }

        // 취소 버튼
        binding.btnReviewReportTargetCancel.setOnClickListener {
            dismiss()
        }

        // 신고하기 버튼 - 리스너 호출
        binding.btnReviewReportTargetConfirm.setOnClickListener {
            selectedTarget?.let { target ->
                listener?.let { l ->
                    // 선택된 대상이 "작성자" 또는 "리뷰 작성자"인지 확인
                    val isStudentReport = target.contains("작성자")
                    l.onReportTargetSelected(target, isStudentReport)
                    dismiss()
                } ?: Log.e("ReportTargetDialog", "Listener is null!")
            } ?: Log.e("ReportTargetDialog", "selectedTarget is null!")
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

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    companion object {
        fun newInstance(position: Int): ReportTargetDialogFragment {
            val fragment = ReportTargetDialogFragment()
            val args = Bundle().apply {
                putInt("position", position)
            }
            fragment.arguments = args
            return fragment
        }
    }
}