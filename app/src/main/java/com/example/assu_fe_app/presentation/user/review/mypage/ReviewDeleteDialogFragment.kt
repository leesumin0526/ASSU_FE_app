package com.example.assu_fe_app.presentation.user.review.mypage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
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

        binding.btnDeleteCancel.setOnClickListener {
            onDestroyView()
        }

        binding.btnDeleteConfirm.setOnClickListener {
            val position = arguments?.getInt("position") ?: return@setOnClickListener
            (activity as? OnReviewDeleteConfirmedListener)?.onReviewDeleteConfirmed(position)
            dismiss()
        }
        return binding.root


    }

//    override fun onStart() {
//        super.onStart()
//
//        dialog?.window?.setLayout(
//            (resources.displayMetrics.widthPixels * 0.879).toInt(), // 너비를 화면의 85%로 설정
//            ViewGroup.LayoutParams.WRAP_CONTENT
//        )
//    }

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


interface OnItemClickListener{
    fun onClick(position: Int)
}

interface OnReviewDeleteConfirmedListener {
    fun onReviewDeleteConfirmed(position: Int)
}