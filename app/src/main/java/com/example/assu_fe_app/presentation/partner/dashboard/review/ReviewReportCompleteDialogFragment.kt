package com.example.assu_fe_app.presentation.partner.dashboard.review

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.assu_fe_app.databinding.FragmentReviewReportCompleteDialogBinding
import com.example.assu_fe_app.presentation.common.report.OnReviewReportCompleteListener

class ReviewReportCompleteDialogFragment : DialogFragment() {

    private var _binding: FragmentReviewReportCompleteDialogBinding? = null
    private val binding get() = _binding!!

    private var listener: OnReviewReportCompleteListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewReportCompleteDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)


        // Navigation Component를 사용하는 경우, NavHostFragment의 childFragmentManager에서 현재 활성 Fragment를 찾음
        val navHostFragment = parentFragment
        if (navHostFragment != null) {
            val currentFragment = navHostFragment.childFragmentManager.fragments.firstOrNull()

            if (currentFragment is OnReviewReportCompleteListener) {
                listener = currentFragment
            }
        }

        // 여전히 리스너가 없다면 다른 방법들 시도
        if (listener == null) {
            // 부모 프래그먼트 확인
            if (parentFragment is OnReviewReportCompleteListener) {
                listener = parentFragment as OnReviewReportCompleteListener
            }
            // Activity 확인
            else if (context is OnReviewReportCompleteListener) {
                listener = context
            } else {
                Log.e("ReviewReportCompleteDialog", "No listener found! Neither currentFragment, parentFragment nor context implements OnReviewReportCompleteListener")
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val position = arguments?.getInt("position") ?: -1
        val isStudentReport = arguments?.getBoolean("isStudentReport", false) ?: false

        // 텍스트 변경
        if (isStudentReport) {
            binding.tvCompleteTitle.text = "리뷰 작성자에 대한 신고가\n완료되었습니다!"
            binding.tvCompleteDescription.text = "신고 직후 해당 사용자가 작성한 모든 리뷰는 비공개 처리되며, 해당 사실이 작성자에게 고지되지 않습니다."
        } else {
            binding.tvCompleteTitle.text = "리뷰의 신고가\n완료되었습니다!"
            binding.tvCompleteDescription.text = "신고 직후 해당 리뷰는 비공개 처리되며, 부적절한 사유로 신고한 경우 불이익이 있을수도 있습니다."
        }

        // 확인 버튼 클릭 시
        binding.btnConfirm.setOnClickListener {
            listener?.let { l ->
                l.onReviewReportComplete(position)
                dismiss()
            } ?: Log.e("ReviewReportCompleteDialog", "Listener is null!")
        }

        // 닫기(X) 버튼 클릭 시
        binding.ivCloseButton.setOnClickListener {
            listener?.let { l ->
                l.onReviewReportComplete(position)
                dismiss()
            } ?: Log.e("ReviewReportCompleteDialog", "Listener is null!")
        }
    }


    override fun onResume() {
        super.onResume()
        // 다이얼로그 너비 조절
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
        fun newInstance(position: Int, isStudentReport: Boolean = false): ReviewReportCompleteDialogFragment {
            val fragment = ReviewReportCompleteDialogFragment()
            val args = Bundle().apply {
                putInt("position", position)
                putBoolean("isStudentReport", isStudentReport)
            }
            fragment.arguments = args
            return fragment
        }
    }
}