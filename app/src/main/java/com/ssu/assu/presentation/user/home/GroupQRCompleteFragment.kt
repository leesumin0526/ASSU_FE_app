package com.ssu.assu.presentation.user.home

import androidx.activity.OnBackPressedCallback
import com.ssu.assu.databinding.FragmentGroupQrCompleteBinding
import com.ssu.assu.presentation.base.BaseFragment
import com.ssu.assu.R

class GroupQRCompleteFragment : BaseFragment<FragmentGroupQrCompleteBinding>(R.layout.fragment_group_qr_complete){
    override fun initView() {
        binding.ivCross.setOnClickListener {
            // Activity 종료 후 HomeFragment가 있는 이전 화면으로 돌아감
            requireActivity().finish()
        }
        val callback: Any = object : OnBackPressedCallback(true) { // true로 콜백을 활성화합니다.
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            callback as OnBackPressedCallback
        )
    }

    override fun initObserver() {

    }

}