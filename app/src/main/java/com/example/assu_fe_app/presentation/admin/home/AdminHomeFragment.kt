package com.example.assu_fe_app.presentation.admin.home

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.chatting.request.CreateChatRoomRequestDto
import com.example.assu_fe_app.data.dto.partner_admin.home.PartnershipContractItem
import com.example.assu_fe_app.databinding.FragmentAdminHomeBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.chatting.ChattingActivity
import com.example.assu_fe_app.presentation.common.contract.PartnershipContractDialogFragment
import com.example.assu_fe_app.presentation.common.notification.NotificationActivity
import com.example.assu_fe_app.ui.chatting.ChattingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.example.assu_fe_app.data.manager.TokenManager
import com.example.assu_fe_app.ui.partnership.PartnershipViewModel


@AndroidEntryPoint
class AdminHomeFragment :
    BaseFragment<FragmentAdminHomeBinding>(R.layout.fragment_admin_home) {
    private val vm: HomeViewModel by viewModels()

    private val chattingViewModel: ChattingViewModel by viewModels()

    private val partnershipViewModel: PartnershipViewModel by viewModels()

    lateinit var tokenManager: TokenManager

    override fun initObserver() {
        // 채팅방 생성 상태 수집
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                chattingViewModel.createRoomState.collect { state ->
                    when (state) {
                        is ChattingViewModel.CreateRoomUiState.Loading -> {
                            // 필요시 로딩 UI 처리(버튼 비활성화 등)
                            binding.btnRecommendInquiry.isEnabled = false
                        }

                        is ChattingViewModel.CreateRoomUiState.Success -> {
                            binding.btnRecommendInquiry.isEnabled = true

                            val roomId = state.data.roomId

                            val intent = Intent(requireContext(), ChattingActivity::class.java).apply {
                                putExtra("roomId", roomId)
                            }

                            startActivity(intent)
                            Toast.makeText(
                                requireContext(),
                                "채팅방 생성 성공: ${state}",
                                Toast.LENGTH_SHORT
                            ).show()

                            // 한 번 처리 후 상태 리셋
                            chattingViewModel.resetCreateState()
                        }

                        is ChattingViewModel.CreateRoomUiState.Fail -> {
                            binding.btnRecommendInquiry.isEnabled = true
                            Toast.makeText(
                                requireContext(),
                                "채팅방 생성 실패: ${state.code}",
                                Toast.LENGTH_SHORT
                            ).show()
                             Log.e("AdminHomeFragment", "Fail code=${state.code}, msg=${state.message}")
                            chattingViewModel.resetCreateState()
                        }

                        is ChattingViewModel.CreateRoomUiState.Error -> {
                            binding.btnRecommendInquiry.isEnabled = true
                            Toast.makeText(
                                requireContext(),
                                "에러: ${state.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                            chattingViewModel.resetCreateState()
                        }

                        ChattingViewModel.CreateRoomUiState.Idle -> Unit
                    }
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                partnershipViewModel.getPartnershipPartnerListUiState.collect { state ->
                    when (state) {
                        is PartnershipViewModel.PartnershipPartnerListUiState.Success -> {
                            val list = state.data

                            if(list.isEmpty()) {
                                binding.btnAdminHomeViewAll.visibility = View.INVISIBLE
                                binding.tvNoPartnerList.visibility = View.VISIBLE
                            } else {
                                binding.tvNoPartnerList.visibility = View.GONE
                            }

                            // 1번째 카드
                            if (list.isNotEmpty()) {
                                val item1 = list[0]
                                binding.tvPartnerName1.text = item1.shopName
                                binding.tvBenefitDescription1.text =
                                    item1.content.firstOrNull()?.type ?: "제휴 혜택 없음"
                                binding.tvBenefitPeriod1.text =
                                    "${item1.startDate} ~ ${item1.endDate}"
                                binding.adminHomeListItem1.visibility = View.VISIBLE

                                binding.adminHomeListItem1.setOnClickListener {
                                    val dialog = PartnershipContractDialogFragment(
                                        item1.content.map { c ->
                                            PartnershipContractItem.Service.ByPeople(
                                                c.people.toInt(), // UI 모델이 Int면 toInt(), Long으로 바꾸면 그대로 사용
                                                c.type
                                            )
                                        }
                                    )
                                    dialog.show(parentFragmentManager, "PartnershipContentDialog")
                                }
                            } else {
                                binding.adminHomeListItem1.visibility = View.GONE
                            }

                            // 2번째 카드
                            if (list.size > 1) {
                                val item2 = list[1]
                                binding.tvAdminName2.text = item2.shopName
                                binding.tvBenefitDescription2.text =
                                    item2.content.firstOrNull()?.type ?: "제휴 혜택 없음"
                                binding.tvBenefitPeriod2.text =
                                    "${item2.startDate} ~ ${item2.endDate}"
                                binding.adminHomeListItem2.visibility = View.VISIBLE

                                binding.adminHomeListItem2.setOnClickListener {
                                    val dialog = PartnershipContractDialogFragment(
                                        item2.content.map { c ->
                                            PartnershipContractItem.Service.ByPeople(
                                                c.people.toInt(),
                                                c.type
                                            )
                                        }
                                    )
                                    dialog.show(parentFragmentManager, "PartnershipContentDialog")
                                }
                            } else {
                                binding.adminHomeListItem2.visibility = View.GONE
                            }
                        }

                        // ✅ 나머지 모든 상태에선 가려두기
                        is PartnershipViewModel.PartnershipPartnerListUiState.Loading,
                        is PartnershipViewModel.PartnershipPartnerListUiState.Idle,
                        is PartnershipViewModel.PartnershipPartnerListUiState.Fail,
                        is PartnershipViewModel.PartnershipPartnerListUiState.Error -> {
                            binding.adminHomeListItem1.visibility = View.GONE
                            binding.adminHomeListItem2.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        vm.refreshBell()
        partnershipViewModel.getProposalPartnerList(isAll = false) // true면 전체
    }

    override fun initView() {

        tokenManager = TokenManager(requireContext())
        val userName = tokenManager.getUserName() ?: "사용자"

        binding.tvAdminHomeName.text = if (userName.isNotEmpty()) {
            "안녕하세요, ${userName}님!"
        } else {
            "안녕하세요, 사용자님!"
        }

        // 🔽 전체 조회 버튼
        binding.btnAdminHomeViewAll.setOnClickListener {
            // ✅ 전체 조회 API 호출
            partnershipViewModel.getProposalPartnerList(isAll = true)
        }

        binding.ivAdminHomeNotification.setOnClickListener {
            NotificationActivity.start(requireContext(), NotificationActivity.Role.ADMIN)
        }

        // 벨 아이콘 상태 구독
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.bellFilled.collect { exists ->
                    binding.ivAdminHomeNotification.setImageResource(
                        if (exists) R.drawable.ic_bell_fill else R.drawable.ic_bell_unfill
                    )
                }
            }
        }

        binding.tvContractPassiveRegister.setOnClickListener { view ->
            Navigation.findNavController(view).navigate(R.id.action_admin_home_to_contract_passive_register)
        }

        binding.btnRecommendInquiry.setOnClickListener {
            val req = CreateChatRoomRequestDto(
                //TODO : 유저 정보 받아오기
                adminId = 1L,
                partnerId = 5L
            )
            chattingViewModel.createRoom(req)

        }
    }
}