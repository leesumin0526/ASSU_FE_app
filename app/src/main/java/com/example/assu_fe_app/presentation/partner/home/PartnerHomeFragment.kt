package com.example.assu_fe_app.presentation.partner.home

import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import com.example.assu_fe_app.presentation.common.contract.PartnershipContractDialogFragment
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.chatting.request.CreateChatRoomRequestDto
import com.example.assu_fe_app.data.dto.partner_admin.home.PartnershipContractItem
import com.example.assu_fe_app.data.dto.partnership.response.CriterionType
import com.example.assu_fe_app.data.dto.partnership.response.OptionType
import com.example.assu_fe_app.data.manager.TokenManager
import com.example.assu_fe_app.databinding.FragmentPartnerHomeBinding
import com.example.assu_fe_app.domain.model.admin.GetProposalAdminListModel
import com.example.assu_fe_app.presentation.admin.home.HomeViewModel
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.chatting.ChattingActivity
import com.example.assu_fe_app.presentation.common.notification.NotificationActivity
import com.example.assu_fe_app.ui.chatting.ChattingViewModel
import com.example.assu_fe_app.ui.partnership.PartnershipViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue
import kotlin.jvm.java

@AndroidEntryPoint
class PartnerHomeFragment :
    BaseFragment<FragmentPartnerHomeBinding>(R.layout.fragment_partner_home) {

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
                            binding.viewPartnerHomeCardBg.isEnabled = false

                        }

                        is ChattingViewModel.CreateRoomUiState.Success -> {
                            binding.viewPartnerHomeCardBg.isEnabled = true

                            val roomId = state.data.roomId

                            val intent =
                                Intent(requireContext(), ChattingActivity::class.java).apply {
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
                            binding.viewPartnerHomeCardBg.isEnabled = true
                            Toast.makeText(
                                requireContext(),
                                "채팅방 생성 실패: ${state.code}",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e(
                                "AdminHomeFragment",
                                "Fail code=${state.code}, msg=${state.message}"
                            )
                            chattingViewModel.resetCreateState()
                        }

                        is ChattingViewModel.CreateRoomUiState.Error -> {
                            binding.viewPartnerHomeCardBg.isEnabled = true
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
                partnershipViewModel.getPartnershipAdminListUiState.collect { state ->
                    when (state) {
                        is PartnershipViewModel.PartnershipAdminListUiState.Success -> {
                            val data = state.data

                            if(data.isEmpty()) {
                                binding.btnPartnerHomeViewAll.visibility = View.INVISIBLE
//                                binding.tvNoPartnerList.visibility = View.VISIBLE
                            } else {
//                                binding.tvNoPartnerList.visibility = View.GONE
                            }

                            val firstItem = data.getOrNull(0)
                            if (firstItem != null) {
                                bindAdminItem(
                                    binding.partnerHomeListItem1,
                                    binding.tvAdminName1,
                                    binding.tvBenefitDescription1,
                                    binding.tvBenefitPeriod1,
                                    firstItem
                                )
                            } else {
                                binding.partnerHomeListItem1.isVisible = false
                            }

                            val secondItem = data.getOrNull(1)
                            if (secondItem != null) {
                                bindAdminItem(
                                    binding.partnerHomeListItem2,
                                    binding.tvAdminName2,
                                    binding.tvBenefitDescription2,
                                    binding.tvBenefitPeriod2,
                                    secondItem
                                )
                            } else {
                                binding.partnerHomeListItem2.isVisible = false
                            }

                            // 전체보기 버튼은 데이터가 1건 이상일 때만 활성화
                            binding.btnPartnerHomeViewAll.isEnabled = data.isNotEmpty()
                        }

                        is PartnershipViewModel.PartnershipAdminListUiState.Loading -> {
                            binding.partnerHomeListItem1.isVisible = false
                            binding.partnerHomeListItem2.isVisible = false
                            binding.btnPartnerHomeViewAll.isEnabled = false
                        }

                        is PartnershipViewModel.PartnershipAdminListUiState.Fail -> {
                            Toast.makeText(requireContext(), "서버 실패: ${state.message}", Toast.LENGTH_SHORT).show()
                            Log.e("PartnerHomeFragment", "Fail code=${state.code}, message=${state.message}")
                            binding.partnerHomeListItem1.isVisible = false
                            binding.partnerHomeListItem2.isVisible = false
                            binding.btnPartnerHomeViewAll.isEnabled = false
                        }

                        is PartnershipViewModel.PartnershipAdminListUiState.Error -> {
                            Toast.makeText(requireContext(), "에러: ${state.message}", Toast.LENGTH_SHORT).show()
                            Log.e("PartnerHomeFragment", "Error message=${state.message}")
                            binding.partnerHomeListItem1.isVisible = false
                            binding.partnerHomeListItem2.isVisible = false
                            binding.btnPartnerHomeViewAll.isEnabled = false
                        }

                        PartnershipViewModel.PartnershipAdminListUiState.Idle -> Unit
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        vm.refreshBell()
    }

    override fun initView() {

        partnershipViewModel.getProposalAdminList(isAll = false)

        tokenManager = TokenManager(requireContext())
        val userName = tokenManager.getUserName() ?: "사용자"

        binding.tvPartnerHomeName.text = if (userName.isNotEmpty()) {
            "안녕하세요, ${userName}님!"
        } else {
            "안녕하세요, 사용자님!"
        }

        binding.btnPartnerHomeViewAll.setOnClickListener { view ->
            Navigation.findNavController(view).navigate(R.id.action_partner_home_to_partner_view_admin_list)
        }

        binding.ivPartnerHomeNotification.setOnClickListener {
            NotificationActivity.start(requireContext(), NotificationActivity.Role.PARTNER)
        }

        // 벨 아이콘 상태 구독
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.bellFilled.collect { exists ->
                    binding.ivPartnerHomeNotification.setImageResource(
                        if (exists) R.drawable.ic_bell_fill else R.drawable.ic_bell_unfill
                    )
                }
            }
        }

        binding.viewPartnerHomeCardBg.setOnClickListener {
            val req = CreateChatRoomRequestDto(
                //TODO : 유저 정보 받아오기
                adminId = 1L,
                partnerId = 5L
            )
            chattingViewModel.createRoom(req)
        }
    }

    private fun bindAdminItem(
        bindingItem: ViewGroup,
        titleView: TextView,
        descView: TextView,
        periodView: TextView,
        item: GetProposalAdminListModel
    ) {
        titleView.text = item.adminId.toString() // TODO: 실제 가맹점명 필드 있으면 교체
        periodView.text = "${item.partnershipPeriodStart} ~ ${item.partnershipPeriodEnd}"

        // 옵션 설명 만들기
        val option = item.options.firstOrNull()
        descView.text = if (option != null) {
            when (option.optionType) {
                OptionType.SERVICE -> when (option.criterionType) {
                    CriterionType.HEADCOUNT -> "${option.people}명당 ${option.goods.firstOrNull()?.goodsName ?: "상품"} 제공"
                    CriterionType.PRICE -> "${option.cost}원 이상 주문 시 ${option.goods.firstOrNull()?.goodsName ?: "상품"} 제공"
                }
                OptionType.DISCOUNT -> when (option.criterionType) {
                    CriterionType.HEADCOUNT -> "${option.people}명 이상 ${option.discountRate}% 할인"
                    CriterionType.PRICE -> "${option.cost}원 이상 주문 시 ${option.discountRate}% 할인"
                }
            }
        } else {
            "제휴 혜택 없음"
        }

        bindingItem.visibility = View.VISIBLE
        bindingItem.setOnClickListener {
            val dialog = PartnershipContractDialogFragment(
//                item.options.map { opt ->
//                    // 여기서도 OptionType/ CriterionType에 따라 적절한 PartnershipContractItem 변환 가능
//                    PartnershipContractItem.Service.ByPeople(opt.people, opt.category)
//                }
            )
            dialog.show(parentFragmentManager, "PartnershipContentDialog")
        }
    }
}