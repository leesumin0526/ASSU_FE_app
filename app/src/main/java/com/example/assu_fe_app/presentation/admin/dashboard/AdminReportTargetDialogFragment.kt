package com.example.assu_fe_app.presentation.admin.dashboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.assu_fe_app.R
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.assu_fe_app.databinding.FragmentAdminReportTargetDialogBinding
import com.example.assu_fe_app.presentation.common.report.OnReportTargetSelectedListener

class AdminReportTargetDialogFragment : DialogFragment() {

    private var _binding: FragmentAdminReportTargetDialogBinding? = null
    private val binding get() = _binding!!

    private var listener: OnReportTargetSelectedListener? = null
    private var selectedTarget: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminReportTargetDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Activity가 리스너를 구현했는지 확인
        if (context is OnReportTargetSelectedListener) {
            listener = context
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // X 버튼 → 닫기
        binding.btnAdminReportTargetCross.setOnClickListener {
            dismiss()
        }

        // 선택 항목 클릭
        val optionViews = listOf(binding.tvAdminReportReviewWriter, binding.tvAdminReportReview)

        // 초기화
        optionViews.forEach {
            it.isSelected = false
            it.setTextColor(ContextCompat.getColor(requireContext(), R.color.assu_font_sub))
        }

        // 완료 버튼 초기 상태 비활성화
        binding.btnAdminReportTargetConfirm.isEnabled = false

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
                binding.btnAdminReportTargetConfirm.isEnabled = true
            }
        }

        // 취소 버튼
        binding.btnAdminReportTargetCancel.setOnClickListener {
            dismiss()
        }

        // 신고하기 버튼 - Activity의 리스너 호출
        binding.btnAdminReportTargetConfirm.setOnClickListener {
            selectedTarget?.let { target ->
                listener?.onReportTargetSelected(target)
                dismiss()
            }
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
        fun newInstance(position: Int): AdminReportTargetDialogFragment {
            val fragment = AdminReportTargetDialogFragment()
            val args = Bundle().apply {
                putInt("position", position)
            }
            fragment.arguments = args
            return fragment
        }
    }
}