package com.assu.app.presentation.user.mypage

import android.animation.ObjectAnimator
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.assu.app.databinding.FragmentUserMypageFaqBinding

class UserMypageFAQDialogFragment : DialogFragment() {

    private var _binding: FragmentUserMypageFaqBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserMypageFaqBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 뒤로가기 버튼 클릭
        binding.btnFaqBack.setOnClickListener {
            dismiss()
        }

        // FAQ 토글 설정
        setupFAQToggles()
    }

    private fun setupFAQToggles() {
        // FAQ 1
        binding.headerFaq1.setOnClickListener {
            toggleFAQ(binding.contentFaq1, binding.arrowFaq1)
        }

        // FAQ 2
        binding.headerFaq2.setOnClickListener {
            toggleFAQ(binding.contentFaq2, binding.arrowFaq2)
        }

        // FAQ 3
        binding.headerFaq3.setOnClickListener {
            toggleFAQ(binding.contentFaq3, binding.arrowFaq3)
        }

        // FAQ 4
        binding.headerFaq4.setOnClickListener {
            toggleFAQ(binding.contentFaq4, binding.arrowFaq4)
        }
    }

    private fun toggleFAQ(contentView: View, arrowView: View) {
        val isVisible = contentView.visibility == View.VISIBLE
        
        if (isVisible) {
            // 닫기
            contentView.visibility = View.GONE
            rotateArrow(arrowView, 0f)
        } else {
            // 열기
            contentView.visibility = View.VISIBLE
            rotateArrow(arrowView, 180f)
        }
    }

    private fun rotateArrow(arrowView: View, rotation: Float) {
        val animator = ObjectAnimator.ofFloat(arrowView, "rotation", rotation)
        animator.duration = 200
        animator.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
