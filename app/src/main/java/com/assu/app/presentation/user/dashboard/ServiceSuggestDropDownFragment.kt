package com.assu.app.presentation.user.dashboard

import com.assu.app.presentation.base.BaseFragment
import com.assu.app.databinding.FragmentServiceSuggestDropDownBinding
import com.assu.app.R
import com.assu.app.domain.model.suggestion.SuggestionTargetModel

class ServiceSuggestDropDownFragment : BaseFragment<FragmentServiceSuggestDropDownBinding>(R.layout.fragment_service_suggest_drop_down) {

    private var onOptionSelected: ((SuggestionTargetModel) -> Unit)? = null

    fun setOnOptionSelectedListener(listener: (SuggestionTargetModel) -> Unit) {
        onOptionSelected = listener
    }

    override fun initObserver() {

    }

    override fun initView() {
        // 나중에 회원 데이터 받아서 정보 받으면 바꿔줄 것.
//        binding.tvSuggestDropTarget1.setOnClickListener {
//            onOptionSelected?.invoke("총학생회")
//            parentFragmentManager.popBackStack()
//        }
//        binding.tvSuggestDropTarget2.setOnClickListener {
//            onOptionSelected?.invoke("IT대학 학생회")
//            parentFragmentManager.popBackStack()
//        }
//        binding.tvSuggestDropTarget3.setOnClickListener {
//            onOptionSelected?.invoke("컴퓨터학부 학생회")
//            parentFragmentManager.popBackStack()
//        }
    }

}