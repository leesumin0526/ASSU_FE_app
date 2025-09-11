package com.example.assu_fe_app.presentation.user.mypage

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentUserMypageBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.login.LoginActivity
import com.example.assu_fe_app.presentation.common.mypage.MypageViewModel
import com.example.assu_fe_app.presentation.user.review.mypage.UserMyReviewActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserMypageFragment
    : BaseFragment<FragmentUserMypageBinding>(R.layout.fragment_user_mypage) {

    private val viewModel: MypageViewModel by viewModels()

    override fun initView() { /* no-op */ }

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
        initClick()
    }

    private fun initClick() {
        binding.clAccountComponent1.setOnClickListener {
            startActivity(Intent(requireContext(), UserMyReviewActivity::class.java))
        }

        // 프로필 수정
        binding.clAccountComponent2.setOnClickListener {
            // TODO: 구현 예정
        }

        // 개인정보 처리방침
        binding.clAccountComponent4.setOnClickListener {
            UserMypagePrivacyDialogFragment()
                .show(childFragmentManager, "PrivacyDialog")
        }

        // FAQ
        binding.clAccountComponent5.setOnClickListener {
            UserMypageFAQDialogFragment()
                .show(childFragmentManager, "FAQDialog")
        }

        // 고객센터
        binding.clAccountComponent6.setOnClickListener {
            UserCustomerServiceDialogFragment()
                .show(childFragmentManager, "CustomerServiceDialog")
        }

        // 로그아웃: 서버에서 unregister 성공 시에만 화면 이동 (observer에서 처리)
        binding.clAccountComponent3.setOnClickListener {
            findNavController().navigate(
                R.id.action_user_mypage_to_mypage_account
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
