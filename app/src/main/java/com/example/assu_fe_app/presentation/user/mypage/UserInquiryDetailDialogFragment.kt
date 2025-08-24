package com.example.assu_fe_app.presentation.user.mypage

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.example.assu_fe_app.data.dto.InquiryItem
import com.example.assu_fe_app.databinding.FragmentUserInquiryDetailBinding

class UserInquiryDetailDialogFragment : DialogFragment() {

    private var _binding: FragmentUserInquiryDetailBinding? = null
    private val binding get() = _binding!!

    private var inquiryItem: InquiryItem? = null

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
        _binding = FragmentUserInquiryDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViews()
        loadInquiryData()
    }

    private fun setupViews() {
        // 뒤로가기 버튼
        binding.btnCsDetailBack.setOnClickListener {
            dismiss()
        }
    }

    private fun loadInquiryData() {
        inquiryItem?.let { inquiry ->
            binding.apply {
                tvDetailTitle.text = inquiry.title
                tvDetailDate.text = inquiry.date
                tvDetailTime.text = inquiry.time
                tvDetailContent.text = inquiry.content
                
                // 답변이 있는 경우에만 답변 섹션 표시
                if (!inquiry.answer.isNullOrEmpty()) {
                    tvDetailAnswer.text = inquiry.answer
                    answerSectionContainer.visibility = View.VISIBLE
                } else {
                    answerSectionContainer.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(inquiryItem: InquiryItem): UserInquiryDetailDialogFragment {
            return UserInquiryDetailDialogFragment().apply {
                this.inquiryItem = inquiryItem
            }
        }
    }
}
