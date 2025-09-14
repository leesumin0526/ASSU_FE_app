package com.example.assu_fe_app.presentation.admin.mypage


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.manager.TokenManager
import com.example.assu_fe_app.databinding.FragmentAdminMypageBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.login.LoginActivity
import com.example.assu_fe_app.presentation.common.mypage.MypageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class AdminMypageFragment : BaseFragment<FragmentAdminMypageBinding>(R.layout.fragment_admin_mypage) {

    @Inject
    lateinit var tokenManager: TokenManager

    private val viewModel: MypageViewModel by viewModels()

    override fun initView(){
        // UI 초기화만 수행
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
        initClick()
    }

    private fun initClick() {
        // 알림 설정
        binding.clAdmAccountComponent1.setOnClickListener {
            AdminMypageAlarmDialogFragment()
                .show(childFragmentManager, "AlarmDialog")
        }

        // 계정관리 페이지 이동
        binding.clAdmAccountComponent2.setOnClickListener {
            findNavController().navigate(
                R.id.action_admin_mypage_to_mypage_account
            )
        }

        // 대기중인 제휴계약서
        binding.clAdmAccountComponent3.setOnClickListener {
            AdminMypagePendingPartnershipDialogFragment()
                .show(childFragmentManager, "PendingPartnershipDialog")
        }
    }

    private fun navigateToLoginAndClear() {
        val intent = Intent(requireContext(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }
}

