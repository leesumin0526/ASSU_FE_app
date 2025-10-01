package com.example.assu_fe_app.presentation.common.mypage

import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentBlockManageBinding
import com.example.assu_fe_app.presentation.base.BaseFragment

class BlockManageFragment : BaseFragment<FragmentBlockManageBinding> (R.layout.fragment_block_manage) {
    override fun initObserver() {

    }

    override fun initView() {
        binding.btnPendingBack.setOnClickListener {
            fragmentManager?.popBackStack()
        }

    }

}