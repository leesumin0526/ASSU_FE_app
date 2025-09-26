package com.example.assu_fe_app.presentation.admin.dashboard

import com.example.assu_fe_app.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.assu_fe_app.databinding.FragmentReviewReportCompleteDialogBinding

class AdminSuggestionReportCompleteDialogFragment : DialogFragment() {

    private var _binding: FragmentReviewReportCompleteDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewReportCompleteDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 배경을 투명하게 설정
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val position = arguments?.getInt("position") ?: -1

        // 확인 버튼 클릭 시 Activity의 메서드 호출하고 다이얼로그 닫기
        binding.btnConfirm.setOnClickListener {
            (activity as? AdminDashboardSuggestionsActivity)?.onReportCompleteConfirmed(position)
            dismiss()
        }

        // 닫기(X) 버튼 클릭 시 Activity의 메서드 호출하고 다이얼로그 닫기
        binding.ivCloseButton.setOnClickListener {
            (activity as? AdminDashboardSuggestionsActivity)?.onReportCompleteConfirmed(position)
            dismiss()
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

    companion object {
        fun newInstance(position: Int): AdminSuggestionReportCompleteDialogFragment {
            val fragment = AdminSuggestionReportCompleteDialogFragment()
            val args = Bundle().apply {
                putInt("position", position)
            }
            fragment.arguments = args
            return fragment
        }
    }
}