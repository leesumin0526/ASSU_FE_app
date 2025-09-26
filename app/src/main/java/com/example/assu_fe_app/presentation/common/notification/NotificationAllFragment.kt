package com.example.assu_fe_app.presentation.common.notification

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentNotificationAllBinding
import com.example.assu_fe_app.domain.model.notification.NotificationModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.presentation.admin.dashboard.AdminDashboardSuggestionsActivity
import com.example.assu_fe_app.presentation.partner.PartnerMainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationAllFragment : Fragment(R.layout.fragment_notification_all) {

    private var _binding: FragmentNotificationAllBinding? = null
    private val binding get() = _binding!!
    private val vm: NotificationsViewModel by activityViewModels()
    private lateinit var adapter: NotificationAdapter
    private lateinit var role: NotificationActivity.Role

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNotificationAllBinding.bind(view)
        role = readRoleArg()

        Log.d("RoleCheck", "ARG_ROLE raw=${arguments?.get(ARG_ROLE)}")

        adapter = NotificationAdapter(onClick = ::handleClick)
        binding.rvNotificationAll.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNotificationAll.adapter = adapter

        // 최초 로드
        vm.refresh(status = "all")

        // 목록 상태 구독
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.allState.collectLatest { st ->
                    Log.d("NOTI_UI", "collect allState: items=${st.items.size}, loading=${st.loading}")
                    adapter.submitList(st.items)
                }
            }
        }

        // 네비게이션 이벤트 구독
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.navEvents.collectLatest { ev ->
                    when (ev) {
                        is NotificationsViewModel.NavEvent.ToChatRoom -> {
                            // findNavController().navigate(
                            //     R.id.action_notifications_to_chatRoom,
                            //     bundleOf("roomId" to ev.roomId, "role" to role.name)
                            // )
                        }
                        is NotificationsViewModel.NavEvent.ToPartnerSuggestionDetail -> {
                            Log.d("NavCheck", "ToPartnerSuggestionDetail event received!")
                            if (role == NotificationActivity.Role.ADMIN) {
                                val intent = Intent(requireContext(), AdminDashboardSuggestionsActivity::class.java)
                                startActivity(intent)
                            }
                        }
                        is NotificationsViewModel.NavEvent.ToPartnerProposalDetail -> {
                            val intent = Intent(requireContext(), PartnerMainActivity::class.java).apply {
                                putExtra("nav_dest_id", R.id.partnerChattingFragment)
                            }
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                            startActivity(intent)
                            requireActivity().finish()
                        }
                    }
                }
            }
        }

        // 무한 스크롤
        binding.rvNotificationAll.addOnScrollListener(object : EndlessScrollListener() {
            override fun onLoadMore() = vm.loadMore("all")
        })
    }

    private fun handleClick(item: NotificationModel) {
        vm.onItemClickSmart(item, activeTab = "all")
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_ROLE = "arg_role"
        fun newInstance(role: NotificationActivity.Role) = NotificationAllFragment().apply {
            arguments = Bundle().apply { putSerializable(ARG_ROLE, role) }
        }
    }

    private fun readRoleArg(): NotificationActivity.Role {
        val k = "arg_role"
        return if (android.os.Build.VERSION.SDK_INT >= 33) {
            arguments?.getSerializable(k, NotificationActivity.Role::class.java)
                ?: NotificationActivity.Role.PARTNER
        } else {
            @Suppress("DEPRECATION")
            (arguments?.getSerializable(k) as? NotificationActivity.Role)
                ?: NotificationActivity.Role.PARTNER
        }
    }
}