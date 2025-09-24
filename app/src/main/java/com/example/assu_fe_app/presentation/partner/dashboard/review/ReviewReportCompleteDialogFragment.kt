package com.example.assu_fe_app.presentation.partner.dashboard.review

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.assu_fe_app.databinding.FragmentReviewReportCompleteDialogBinding

class ReviewReportCompleteDialogFragment : DialogFragment() {

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
    private var listener: OnReviewReportCompleteListener? = null

    // 3. onAttach에서 리스너 설정
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // 부모 프래그먼트가 인터페이스를 구현했는지 확인
        if (parentFragment is OnReviewReportCompleteListener) {
            listener = parentFragment as OnReviewReportCompleteListener
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 배경을 투명하게 설정 (XML의 background가 보이도록)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val position = arguments?.getInt("position") ?: -1
        // 확인 버튼 클릭 시 다이얼로그 닫기
        binding.btnConfirm.setOnClickListener {
            listener?.onReviewReportComplete(position)
            dismiss()
        }

        // 닫기(X) 버튼 클릭 시 다이얼로그 닫기
        binding.ivCloseButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        // 다이얼로그 너비 조절 (기존과 동일하게)
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

interface OnReviewReportCompleteListener{
    fun onReviewReportComplete(position: Int)
}