package com.example.assu_fe_app.presentation.user.home

import android.content.Intent
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentUserPartnershipVerifyCompleteBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.login.LoginActivity
import com.example.assu_fe_app.presentation.user.review.writing.UserPhotoReviewActivity
import com.example.assu_fe_app.presentation.user.review.writing.UserStarReviewActivity

class UserPartnershipVerifyCompleteFragment :
    BaseFragment<FragmentUserPartnershipVerifyCompleteBinding>(R.layout.fragment_user_partnership_verify_complete) {

    override fun initView() {
        // X 버튼 클릭 시 홈으로 이동
        binding.ivCross.setOnClickListener {
            // Activity 종료 후 HomeFragment가 있는 이전 화면으로 돌아감
            requireActivity().finish()
        }

        // (선택적으로 리뷰 작성하기 버튼 처리 등)
        binding.btnCheckContract.setOnClickListener {
            val intent = Intent(requireContext(), UserStarReviewActivity::class.java)
            startActivity(intent)
        }
    }

    override fun initObserver() {
        // 옵저버 등록 필요 시 작성
    }
}
