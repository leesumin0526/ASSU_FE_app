package com.example.assu_fe_app.presentation.common.chatting.proposal

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.local.AuthTokenLocalStore
import com.example.assu_fe_app.databinding.FragmentServiceProposalTermWritingBinding
import com.example.assu_fe_app.databinding.ItemServiceProposalSetBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.chatting.ChattingSentProposalFragment
import com.example.assu_fe_app.ui.partnership.PartnershipViewModel
import com.example.assu_fe_app.ui.partnership.WritePartnershipUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ServiceProposalTermWritingFragment
    : BaseFragment<FragmentServiceProposalTermWritingBinding>(R.layout.fragment_service_proposal_term_writing) {
    private val viewModel: PartnershipViewModel by activityViewModels()
    private var isEditMode: Boolean = false

    override fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                // 1. 버튼 활성화 상태를 ViewModel로부터 구독합니다.
                launch {
                    viewModel.isSubmitButtonEnabled.collect { isEnabled ->
                        binding.btnCompleted.isEnabled = isEnabled
                        val tintRes = if (isEnabled) R.color.assu_main else R.color.assu_sub
                        binding.btnCompleted.backgroundTintList =
                            ContextCompat.getColorStateList(requireContext(), tintRes)
                    }
                }

                // 2. API 호출 결과 상태를 구독하여 화면 이동을 처리합니다.
                launch {
                    viewModel.writePartnershipState.collect { state ->
                        when (state) {
                            is WritePartnershipUiState.Loading -> {
                                binding.btnCompleted.isEnabled = false
                            }
                            is WritePartnershipUiState.Success -> {
                                parentFragmentManager.beginTransaction()
                                    .replace(R.id.chatting_fragment_container, ChattingSentProposalFragment())
                                    .addToBackStack(null)
                                    .commit()
                                viewModel.resetWritePartnershipState()
                            }
                            is WritePartnershipUiState.Fail, is WritePartnershipUiState.Error -> {
                                Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                                viewModel.resetWritePartnershipState()
                            }
                            else -> { /* Idle */ }
                        }
                    }
                }
            }
        }
    }

    override fun initView() {
        // ✅ arguments에서 수정 모드 여부 확인
        arguments?.let { bundle ->
            isEditMode = bundle.getBoolean("isEditMode", false)
            Log.d("ServiceProposalTermWritingFragment", "Edit mode: $isEditMode")
        }

        binding.tvCompleted.text = if (isEditMode) "제안서 수정하기" else "제안서 보내기"
        binding.lifecycleOwner = viewLifecycleOwner

        updateSummaryText()

        if (isEditMode) {
            loadExistingValues()
        }

        binding.etFragmentServiceProposalContent2.addTextChangedListener { text ->
            viewModel.partnershipStartDate.value = text.toString()
        }
        binding.ivFragmentServiceProposalContent4.addTextChangedListener { text ->
            viewModel.partnershipEndDate.value = text.toString()
        }
//        binding.etFragmentServiceProposalSign4.addTextChangedListener { text ->
//            viewModel.signature.value = text.toString()
//        } // TODO: (인) 표시 처리에 따라 수정

        binding.btnCompleted.setOnClickListener {
            viewModel.updateSignDate()
            viewModel.onNextButtonClicked()
        }

        binding.ivFragmentServiceProposalBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }
    private fun updateSummaryText() {
        val adminName = viewModel.adminName.value
        val partnerName = viewModel.partnerName.value

        val summaryText = buildString {
            append("위와 같이 ")
            append(adminName.ifEmpty { "-" })
            append("와의\n제휴를 제안합니다.\n\n")
            append("대표 ")
            append(partnerName.ifEmpty { "-" })
        }
        binding.tvFragmentServiceProposalSign2.text = summaryText

        binding.tvFragmentServiceProposalSign3.visibility = View.GONE
        binding.etFragmentServiceProposalSign4.visibility = View.GONE
    }

    private fun loadExistingValues() {
        // ViewModel에서 기존 값 가져와서 EditText에 설정
        val startDate = viewModel.partnershipStartDate.value
        val endDate = viewModel.partnershipEndDate.value
        val signature = viewModel.partnerName.value // 기본값으로 가게 이름 설정

        if (startDate.isNotEmpty()) {
            binding.etFragmentServiceProposalContent2.setText(startDate)
        }
        if (endDate.isNotEmpty()) {
            binding.ivFragmentServiceProposalContent4.setText(endDate)
        }
        if (signature.isNotEmpty()) {
            binding.etFragmentServiceProposalSign4.setText(signature)
        }

        Log.d("ServiceProposalTermWritingFragment",
            "Loaded values - Start: $startDate, End: $endDate, Sign: $signature")
    }

    companion object {
        // ✅ 수정 모드용 newInstance
        fun newInstanceForEdit(): ServiceProposalTermWritingFragment {
            return ServiceProposalTermWritingFragment().apply {
                arguments = Bundle().apply {
                    putBoolean("isEditMode", true)
                }
            }
        }
    }
}