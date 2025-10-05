package com.assu.app.presentation.user.mypage

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.assu.app.R
import com.assu.app.data.local.AuthTokenLocalStore
import com.assu.app.databinding.FragmentUserMypageBinding
import com.assu.app.presentation.base.BaseFragment
import com.assu.app.presentation.common.login.LoginActivity
import com.assu.app.ui.common.mypage.MypageViewModel
import com.assu.app.presentation.user.review.mypage.UserMyReviewActivity
import com.assu.app.ui.profileImage.ProfileImageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UserMypageFragment
    : BaseFragment<FragmentUserMypageBinding>(R.layout.fragment_user_mypage) {

    @Inject
    lateinit var authTokenLocalStore: AuthTokenLocalStore

    private val viewModel: MypageViewModel by viewModels()
    private val profileViewModel: ProfileImageViewModel by viewModels()


    override fun initView() {

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
        binding.tvAccountName.setText(authTokenLocalStore.getUserName())
        binding.tvAccountImageEdit.setText(authTokenLocalStore.getBasicInfoMajor())
        initClick()
    }

    private fun initClick() {
        binding.clAccountComponent1.setOnClickListener {
            startActivity(Intent(requireContext(), UserMyReviewActivity::class.java))
        }

        // 계정관리 페이지 이동
        binding.clAccountComponent3.setOnClickListener {
            findNavController().navigate(
                R.id.action_user_mypage_to_mypage_account
            )
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
            findNavController().navigate(
                R.id.action_user_mypage_to_inquiry
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