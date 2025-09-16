package com.example.assu_fe_app.presentation.partner.mypage

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.manager.TokenManager
import com.example.assu_fe_app.databinding.FragmentPartnerMypageBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.login.LoginActivity
import com.example.assu_fe_app.presentation.common.mypage.MypageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PartnerMypageFragment
    : BaseFragment<FragmentPartnerMypageBinding>(R.layout.fragment_partner_mypage) {

    @Inject
    lateinit var tokenManager: TokenManager

    private val viewModel: MypageViewModel by viewModels()

    override fun initView(){
        binding.tvPartnerAccountName.setText(tokenManager.getUserName())
    }

    override fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.logoutState.collectLatest { state ->
                when (state) {
                    is MypageViewModel.LogoutState.Done -> navigateToLoginAndClear()
                    else -> Unit
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListeners()
    }

    private fun initClickListeners() {
        // 알림 설정
        binding.clPartnerAccountComponent1.setOnClickListener {
            PartnerMypageAlarmDialogFragment()
                .show(childFragmentManager, "AlarmDialog")
        }

        // 계정관리 페이지 이동
        binding.clPartnerAccountComponent2.setOnClickListener {
            findNavController().navigate(
                R.id.action_partner_mypage_to_mypage_account
            )
        }

    }

    private fun navigateToLoginAndClear() {
        val intent = Intent(requireContext(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }

}