package com.example.assu_fe_app.presentation.user.home

import android.content.Intent
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentUserHomeBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.data.manager.TokenManager





class UserHomeFragment :
    BaseFragment<FragmentUserHomeBinding>(R.layout.fragment_user_home){
    private lateinit var tokenManager: TokenManager
    override fun initObserver() {

    }

    override fun initView() {

        tokenManager= TokenManager(requireContext())
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

        val userName = tokenManager.getUserName() ?: "사용자"

        binding.tvHome1.text = "안녕하세요, ${userName}님!"
    }
    private fun navigateToMyPartnershipDetails() {
        findNavController().navigate(R.id.myPartnershipDetailsFragment)
    }

}
