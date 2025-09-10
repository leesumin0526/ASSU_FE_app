package com.example.assu_fe_app.presentation.common.notification

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assu_fe_app.R
import com.example.assu_fe_app.databinding.FragmentNotificationAllBinding
import com.example.assu_fe_app.domain.model.notification.NotificationModel
import androidx.lifecycle.lifecycleScope
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
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

        role = (arguments?.getSerializable(ARG_ROLE) as? NotificationActivity.Role)
            ?: NotificationActivity.Role.PARTNER

        adapter = NotificationAdapter(onClick = ::handleClick)
        binding.rvNotificationAll.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNotificationAll.adapter = adapter

        // 최초 로드
        vm.refresh(status = "all")

        // 목록 상태 구독
        viewLifecycleOwner.lifecycleScope.launch {
            vm.allState.collectLatest { st ->
                android.util.Log.d("NOTI_UI", "collect allState: items=${st.items.size}, loading=${st.loading}")
                adapter.submitList(st.items)
            }
        }

        // 아이템 클릭시 읽음 처리 + 연관 화면으로 이동
        viewLifecycleOwner.lifecycleScope.launch {
            vm.navEvents.collectLatest { ev ->
                when (ev) {
                    is NotificationsViewModel.NavEvent.ToChatRoom -> {
                        // findNavController().navigate(
                        //     R.id.action_notifications_to_chatRoom,
                        //     bundleOf("roomId" to ev.roomId, "role" to role.name)
                        // )
                    }
                    is NotificationsViewModel.NavEvent.ToPartnerSuggestionDetail -> {
                        // findNavController().navigate(
                        //     R.id.action_notifications_to_partnerSuggestionDetail,
                        //     bundleOf("suggestionId" to ev.suggestionId, "role" to role.name)
                        // )
                    }
                    is NotificationsViewModel.NavEvent.ToPartnerProposalDetail -> {
                        // findNavController().navigate(
                        //     R.id.action_notifications_to_partnerProposalDetail,
                        //     bundleOf("proposalId" to ev.proposalId, "role" to role.name)
                        // )
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
        vm.onItemClickAndReload(item, activeTab = "all")
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
}