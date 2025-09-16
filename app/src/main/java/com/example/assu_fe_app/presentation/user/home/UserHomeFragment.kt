package com.example.assu_fe_app.presentation.user.home

import android.content.Intent
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.manager.TokenManager
import com.example.assu_fe_app.databinding.FragmentUserHomeBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.ui.user.UserHomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.launch


@AndroidEntryPoint
class UserHomeFragment :
    BaseFragment<FragmentUserHomeBinding>(R.layout.fragment_user_home){

    private val viewModel: UserHomeViewModel by viewModels()
    
    @Inject
    lateinit var tokenManager: TokenManager

    override fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.stampState.collect { state ->
                when (state) {
                    is UserHomeViewModel.StampUiState.Idle -> {
                        // 초기 상태
                    }
                    is UserHomeViewModel.StampUiState.Loading -> {
                        // 로딩 상태 (필요시 로딩 UI 표시)
                    }
                    is UserHomeViewModel.StampUiState.Success -> {
                        updateStampDisplay(state.stampCount)
                    }
                    is UserHomeViewModel.StampUiState.Error -> {
                        // 에러 발생 시 기본값 표시 또는 에러 처리
                        updateStampDisplay(0)
                    }
                }
            }
        }
    }

    override fun initView() {

        // 제휴 QR 박스 클릭 시 인증 액티비티로 이동
        binding.clHomeQrBox.setOnClickListener {
            val intent = Intent(requireContext(), UserQRVerifyActivity::class.java)
            startActivity(intent)
        }

        binding.tvSeeMoreMyStamp.setOnClickListener {
            navigateToMyPartnershipDetails()
        }
        binding.ivSeeMoreMyStamp.setOnClickListener {
            navigateToMyPartnershipDetails()
        }
    }

    private fun updateStampDisplay(stampCount: Int) {
        // 모든 스탬프 ImageView ID 리스트
        val stampIds = listOf(
            R.id.iv_home_stamp1, R.id.iv_home_stamp2, R.id.iv_home_stamp3,
            R.id.iv_home_stamp4, R.id.iv_home_stamp5, R.id.iv_home_stamp6,
            R.id.iv_home_stamp7, R.id.iv_home_stamp8, R.id.iv_home_stamp9,
            R.id.iv_home_stamp10
        )

        // 모든 스탬프를 먼저 빈 상태로 초기화
        stampIds.forEach { id ->
            val imageView = binding.root.findViewById<ImageView>(id)
            imageView?.setImageResource(R.drawable.ic_home_stamp) // 빈 스탬프 이미지
        }

        // stampCount만큼 채워진 스탬프로 변경
        val actualStampCount = minOf(stampCount, 10) // 최대 10개로 제한
        for (i in 0 until actualStampCount) {
            val imageView = binding.root.findViewById<ImageView>(stampIds[i])
            imageView?.setImageResource(R.drawable.ic_home_stamp_selected) // 채워진 스탬프 이미지
        }
    }

    private fun navigateToMyPartnershipDetails() {
        findNavController().navigate(R.id.myPartnershipDetailsFragment)
    }
}