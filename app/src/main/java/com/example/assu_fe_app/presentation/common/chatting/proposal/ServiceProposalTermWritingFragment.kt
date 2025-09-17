package com.example.assu_fe_app.presentation.common.chatting.proposal

import android.content.res.ColorStateList
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
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
                                // TODO: 로딩 인디케이터 표시
                            }
                            is WritePartnershipUiState.Success -> {
                                // ✅ API 호출이 성공했을 때만 화면을 이동합니다.
                                parentFragmentManager.beginTransaction()
                                    .replace(R.id.chatting_fragment_container, ChattingSentProposalFragment())
                                    .addToBackStack(null)
                                    .commit()
                                viewModel.resetWritePartnershipState() // 상태 리셋
                            }
                            is WritePartnershipUiState.Fail -> {
                                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                                binding.btnCompleted.isEnabled = true // 실패 시 버튼 다시 활성화
                                viewModel.resetWritePartnershipState()
                            }
                            is WritePartnershipUiState.Error -> {
                                Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                                binding.btnCompleted.isEnabled = true // 실패 시 버튼 다시 활성화
                                viewModel.resetWritePartnershipState()
                            }
                            is WritePartnershipUiState.Idle -> {
                                // isSubmitButtonEnabled.collect가 버튼 상태를 관리하므로 여기서는 별도 처리 필요 없음
                            }
                        }
                    }
                }
            }
        }
    }

    override fun initView() {
        Log.d("ServiceProposalTermWritingFragment", "Fragment created")

        binding.lifecycleOwner = viewLifecycleOwner

        binding.etFragmentServiceProposalContent2.addTextChangedListener { text ->
            viewModel.partnershipStartDate.value = text.toString()
        }
        binding.ivFragmentServiceProposalContent4.addTextChangedListener { text ->
            viewModel.partnershipEndDate.value = text.toString()
        }
        binding.etFragmentServiceProposalSign4.addTextChangedListener { text ->
            viewModel.signature.value = text.toString()
        }

        binding.btnCompleted.setOnClickListener {
            viewModel.onNextButtonClicked()
        }

        binding.ivFragmentServiceProposalBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }
}