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

        // 배경을 투명하게 설정
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val position = arguments?.getInt("position") ?: -1

        // 확인 버튼 클릭 시 부모 프래그먼트의 메서드 호출하고 다이얼로그 닫기
        binding.btnConfirm.setOnClickListener {

            listener?.let { l ->
                l.onReviewReportComplete(position)
                dismiss()
            } ?: Log.e("ReviewReportCompleteDialog", "Listener is null!")
        }

        // 닫기(X) 버튼 클릭 시 부모 프래그먼트의 메서드 호출하고 다이얼로그 닫기
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
        fun newInstance(position: Int): ReviewReportCompleteDialogFragment {
            val fragment = ReviewReportCompleteDialogFragment()
            val args = Bundle().apply {
                putInt("position", position)
            }
            fragment.arguments = args
            return fragment
        }
    }
}