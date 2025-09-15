package com.example.assu_fe_app.presentation.common.chatting.proposal

import android.content.res.ColorStateList
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentServiceProposalTermWritingBinding
import com.example.assu_fe_app.databinding.ItemServiceProposalSetBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.chatting.ChattingSentProposalFragment
import com.example.assu_fe_app.ui.partnership.PartnershipViewModel
import com.example.assu_fe_app.ui.partnership.WritePartnershipUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ServiceProposalTermWritingFragment
    : BaseFragment<FragmentServiceProposalTermWritingBinding>(R.layout.fragment_service_proposal_term_writing) {

    private val viewModel: PartnershipViewModel by activityViewModels()

    override fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.writePartnershipState.collect { state ->
                    when(state) {
                        is WritePartnershipUiState.Loading -> {
                            // ✅ 로딩 상태 처리 (예: 버튼 비활성화)
                            binding.btnCompleted.isEnabled = false
                        }
                        is WritePartnershipUiState.Success -> {
                            findNavController().navigate(
                                R.id.action_serviceProposalTermWritingFragment_to_chattingSentProposalFragment
                            )
                            viewModel.resetWritePartnershipState()
                        }
                        is WritePartnershipUiState.Fail -> {
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                            binding.btnCompleted.isEnabled = true
                            viewModel.resetWritePartnershipState()
                        }
                        is WritePartnershipUiState.Error -> {
                            Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                            binding.btnCompleted.isEnabled = true
                            viewModel.resetWritePartnershipState()
                        }
                        else -> {
                            binding.btnCompleted.isEnabled = true
                        }
                    }
                }
            }
        }
    }

    override fun initView() {
        binding.lifecycleOwner = viewLifecycleOwner

        binding.etFragmentServiceProposalSign4.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val hasText = !s.isNullOrBlank()
                val newColor = if (hasText) {
                    ContextCompat.getColor(requireContext(), R.color.assu_main)
                } else {
                    ContextCompat.getColor(requireContext(), R.color.assu_sub) // 기본색으로
                }

                binding.btnCompleted.backgroundTintList = ColorStateList.valueOf(newColor)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.btnCompleted.setOnClickListener {
            viewModel.onNextButtonClicked()
        }

        binding.ivFragmentServiceProposalBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        checkFormValid()
    }

    private fun checkFormValid() {
        val startDateFilled = binding.etFragmentServiceProposalContent2.text?.isNotBlank() == true
        val endDateFilled = binding.ivFragmentServiceProposalContent4.text?.isNotBlank() == true
        val additionalInfoFilled = binding.etFragmentServiceProposalSign4.text?.isNotBlank() == true

        val isValid = startDateFilled && endDateFilled && additionalInfoFilled

        val colorRes = if (isValid) R.color.assu_main else R.color.assu_sub
        binding.btnCompleted.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), colorRes)
        binding.btnCompleted.isEnabled = isValid
    }
}