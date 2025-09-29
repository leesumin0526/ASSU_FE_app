package com.example.assu_fe_app.presentation.common.mypage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentMypageAccountBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.login.LoginActivity
import com.example.assu_fe_app.ui.common.mypage.MypageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MypageAccountFragment
    : BaseFragment<FragmentMypageAccountBinding>(R.layout.fragment_mypage_account) {

    private val viewModel: MypageViewModel by viewModels()

    override fun initView() { /* no-op */ }

    override fun initObserver() {
        // MypageViewModel의 로그아웃 상태 관찰
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.logoutState.collectLatest { state ->
                Log.d("MypageAccount", "MypageViewModel logoutState changed: $state")
                when (state) {
                    is MypageViewModel.LogoutState.Unregistering -> {
                        Log.d("MypageAccount", "Device token unregistering...")
                    }
                    is MypageViewModel.LogoutState.LoggingOut -> {
                        Log.d("MypageAccount", "Logging out to server...")
                    }
                    is MypageViewModel.LogoutState.Done -> {
                        Log.d("MypageAccount", "Logout completed, navigating to login")
                        Toast.makeText(requireContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                        navigateToLoginAndClear()
                    }
                    is MypageViewModel.LogoutState.Error -> {
                        Log.e("MypageAccount", "Logout error: ${state.msg}")
                        Toast.makeText(requireContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                        navigateToLoginAndClear() // UX 계속 진행
                    }
                    else -> {
                        Log.d("MypageAccount", "Other logout state: $state")
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClick()
    }

    private fun initClick() {

        binding.btnPendingBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.clAdmAccountComponent1.setOnClickListener {
            findNavController().navigate(R.id.action_my_page_account_to_block_manage_fragment)
        }


        binding.clAdmAccountComponent2.setOnClickListener {
            Log.d("MypageAccount", "Logout button clicked")
            // 통합된 로그아웃 함수 호출
            viewModel.logoutAndUnregisterFCMToken()
        }

        binding.clAdmAccountComponent3.setOnClickListener {
            Log.d("MypageAccount", "Secession button clicked")
            try {
                val dialog = SecessionDialogFragment.newInstance()
                dialog.show(childFragmentManager, "SecessionDialog")
                Log.d("MypageAccount", "SecessionDialog shown successfully")
            } catch (e: Exception) {
                Log.e("MypageAccount", "Error showing dialog: ${e.message}", e)
            }
        }
    }

    private fun navigateToLoginAndClear() {
        Log.d("MypageAccount", "Navigating to LoginActivity and clearing task stack")
        val intent = Intent(requireContext(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        Log.d("MypageAccount", "LoginActivity started")
    }
}