package com.example.assu_fe_app.presentation.user.review.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentReviewDeleteDialogBinding

class ReviewDeleteDialogFragment : DialogFragment() {
    private var _binding: FragmentReviewDeleteDialogBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewDeleteDialogBinding.inflate(inflater, container, false)

        binding.ivCloseButton.setOnClickListener {
            dismiss()
        }

        binding.btnDeleteConfirm.setOnClickListener {
            val position = arguments?.getInt("position") ?: return@setOnClickListener
            (activity as? OnReviewDeleteConfirmedListener)?.onReviewDeleteConfirmed(position)
            dismiss()
        }
        return binding.root


    }

    override fun onResume() {
        super.onResume()
        val displayMetrics = resources.displayMetrics
        val width = displayMetrics.widthPixels
        val dialogWidth = (width * 0.8369f).toInt()
        dialog?.window?.setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        dialog?.window?.setBackgroundDrawableResource(R.drawable.bg_review_delete_dialog)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {
        fun newInstance(position: Int): ReviewDeleteDialogFragment {
            val fragment = ReviewDeleteDialogFragment()
            val args = Bundle().apply {
                putInt("position", position)
            }
            fragment.arguments = args
            return fragment
        }
    }

}


// 콜백 리스너 선언
interface OnReviewDeleteConfirmedListener {
    fun onReviewDeleteConfirmed(position: Int)
}