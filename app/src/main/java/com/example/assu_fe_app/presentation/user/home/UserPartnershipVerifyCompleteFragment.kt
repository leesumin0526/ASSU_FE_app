package com.example.assu_fe_app.presentation.user.home

import android.content.Intent
import androidx.fragment.app.activityViewModels
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentUserPartnershipVerifyCompleteBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.login.LoginActivity
import com.example.assu_fe_app.presentation.user.UserMainActivity
import com.example.assu_fe_app.presentation.user.review.writing.UserPhotoReviewActivity
import com.example.assu_fe_app.presentation.user.review.writing.UserStarReviewActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlin.jvm.java

@AndroidEntryPoint
class UserPartnershipVerifyCompleteFragment :
    BaseFragment<FragmentUserPartnershipVerifyCompleteBinding>(R.layout.fragment_user_partnership_verify_complete) {


        override fun initView() {
            // X 버튼 클릭 시 홈으로 이동
            binding.ivCross.setOnClickListener {
                // Activity 종료 후 HomeFragment가 있는 이전 화면으로 돌아감
                requireActivity().finish()
            }

            binding

            binding

            // (선택적으로 리뷰 작성하기 버튼 처리 등)
            binding.btnCheckContract.setOnClickListener {
                val intent = Intent(requireActivity(), UserMainActivity::class.java).apply {
                    putExtra("nav_dest_id", R.id.myPartnershipDetailsFragment)
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                }

                // UserMainActivity 시작
                startActivity(intent)
                activity?.finish()
            }
        }

        override fun initObserver() {
            // 옵저버 등록 필요 시 작성
        }
}
