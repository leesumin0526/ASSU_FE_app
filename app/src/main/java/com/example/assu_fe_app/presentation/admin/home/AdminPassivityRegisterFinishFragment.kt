package com.example.assu_fe_app.presentation.admin.home

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentAdminPassiveRegisterFinishBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminPassivityRegisterFinishFragment : BaseFragment<FragmentAdminPassiveRegisterFinishBinding>(R.layout.fragment_admin_passive_register_finish) {
    override fun initObserver() {
    }

    override fun initView() {
        binding.ivCross.setOnClickListener { view ->
            findNavController().navigate(R.id.action_admin_passive_register_finish_to_admin_home)
        }

        binding.btnCheckContract.setOnClickListener {
            val nav = findNavController()
            findNavController().popBackStack(R.id.adminHomeFragment, /*inclusive=*/false)

            // 1) 목적지 도착 감지 리스너 먼저 붙이고
            val listener = object : NavController.OnDestinationChangedListener {
                override fun onDestinationChanged(
                    controller: NavController,
                    destination: NavDestination,
                    arguments: Bundle?
                ) {
                    if (destination.id == R.id.adminMyPageFragment) {
                        val handle = controller.getBackStackEntry(R.id.adminMyPageFragment).savedStateHandle
                        handle["openPendingDialog"] = true
                        handle["openPendingTargetId"] = /* 열고 싶은 paperId or null */

                            controller.removeOnDestinationChangedListener(this)
                    }
                }
            }
            nav.addOnDestinationChangedListener(listener)

            // 2) 네비게이션 navigate() 대신 BottomNav 탭 전환으로 이동
            val bottomNav = requireActivity()
                .findViewById<BottomNavigationView>(R.id.bottom_navigation_view) // AdminMainActivity의 뷰 id와 동일해야 함
            // 리스너에 막혀도 동작하는 안전한 호출
            bottomNav.menu.performIdentifierAction(R.id.adminMyPageFragment, 0) // <- 메뉴 아이디 사용!
            bottomNav.menu.findItem(R.id.adminMyPageFragment)?.isChecked = true
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