package com.example.assu_fe_app.presentation.common.notification

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentNotificationUnreadBinding
import com.example.assu_fe_app.domain.model.notification.NotificationModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.example.assu_fe_app.presentation.admin.dashboard.AdminDashboardSuggestionsActivity
import com.example.assu_fe_app.presentation.partner.PartnerMainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationUnreadFragment : Fragment(R.layout.fragment_notification_unread) {

    private var _binding: FragmentNotificationUnreadBinding? = null
    private val binding get() = _binding!!
    private val vm: NotificationsViewModel by activityViewModels()
    private lateinit var adapter: NotificationAdapter
    private lateinit var role: NotificationActivity.Role

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNotificationUnreadBinding.bind(view)

        role = (arguments?.getSerializable(ARG_ROLE) as? NotificationActivity.Role)
            ?: NotificationActivity.Role.PARTNER

        adapter = NotificationAdapter(onClick = ::handleClick)
        binding.rvNotificationUnread.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNotificationUnread.adapter = adapter

        vm.refresh(status = "unread")

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.unreadState.collectLatest { st ->
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

        binding.rvNotificationUnread.addOnScrollListener(object : EndlessScrollListener() {
            override fun onLoadMore() = vm.loadMore("unread")
        })
    }

    private fun handleClick(item: NotificationModel) {
        //  미읽음이면 mark + navigate, 읽음이면 navigate만 (뷰모델 내부에서 처리)
        vm.onItemClickSmart(item, activeTab = "unread")
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_ROLE = "arg_role"
        fun newInstance(role: NotificationActivity.Role) = NotificationUnreadFragment().apply {
            arguments = Bundle().apply { putSerializable(ARG_ROLE, role) }
        }
    }
}