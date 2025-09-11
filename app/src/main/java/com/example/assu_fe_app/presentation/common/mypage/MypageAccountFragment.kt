package com.example.assu_fe_app.presentation.common.mypage

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentMypageAccountBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MypageAccountFragment
    : BaseFragment<FragmentMypageAccountBinding>(R.layout.fragment_mypage_account) {

    private val viewModel: MypageViewModel by viewModels()

    override fun initView() { /* no-op */ }

    override fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.logoutState.collectLatest { state ->
                when (state) {
                    is MypageViewModel.LogoutState.Done -> navigateToLoginAndClear()
                    is MypageViewModel.LogoutState.Error -> {
                        // 필요 시 토스트/다이얼로그
                        navigateToLoginAndClear() // UX 계속 진행
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClick()
    }

    private fun initClick() {
        binding.clAdmAccountComponent1.setOnClickListener {
            viewModel.logoutAndUnregister()
        }

        binding.btnPendingBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.clAdmAccountComponent2.setOnClickListener {
            SecessionDialogFragment.newInstance()
                .show(childFragmentManager, "SecessionDialog")
        }
    }

    private fun navigateToLoginAndClear() {
        val intent = Intent(requireContext(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }
}