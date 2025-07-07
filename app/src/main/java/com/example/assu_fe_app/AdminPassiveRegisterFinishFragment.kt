package com.example.assu_fe_app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.databinding.FragmentAdminPassiveRegisterFinishBinding
import com.example.assu_fe_app.presentation.base.BaseFragment


class AdminPassiveRegisterFinishFragment : BaseFragment<FragmentAdminPassiveRegisterFinishBinding>(R.layout.fragment_admin_passive_register_finish) {
    override fun initObserver() {
    }

    override fun initView() {
        binding.ivCross.setOnClickListener { view ->
            findNavController().navigate(R.id.action_admin_passive_register_finish_to_admin_home)
        }
    }

    override fun onResume() {
        super.onResume()
        // 바텀 네비게이션 숨기기
        requireActivity().findViewById<View>(R.id.bottom_navigation_view).visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        // 프래그먼트가 종료되면 다시 보이기
        requireActivity().findViewById<View>(R.id.bottom_navigation_view).visibility = View.VISIBLE
    }
}