package com.example.assu_fe_app.presentation.partner.mypage

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentPartnerMypageBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.login.LoginActivity
import com.example.assu_fe_app.ui.auth.LoginViewModel
import com.example.assu_fe_app.data.manager.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.example.assu_fe_app.presentation.common.mypage.MypageViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PartnerMypageFragment
    : BaseFragment<FragmentPartnerMypageBinding>(R.layout.fragment_partner_mypage) {

    @Inject
    lateinit var tokenManager: TokenManager

    private val loginViewModel: LoginViewModel by viewModels()

    private val viewModel: MypageViewModel by viewModels()

    override fun initView(){
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

        // 로그아웃 창
        binding.clPartnerAccountComponent2.setOnClickListener {
            // 서버에 로그아웃 API 호출 후 토큰 삭제 및 로그인 화면으로 이동
            loginViewModel.logout()
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