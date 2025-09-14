package com.example.assu_fe_app.presentation.admin.home

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
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.chatting.request.CreateChatRoomRequestDto
import com.example.assu_fe_app.data.dto.partner_admin.home.PartnershipContractItem
import com.example.assu_fe_app.data.dto.partnership.PartnershipContractData
import com.example.assu_fe_app.data.dto.partnership.response.CriterionType
import com.example.assu_fe_app.data.dto.partnership.response.OptionType
import com.example.assu_fe_app.databinding.FragmentAdminHomeBinding
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.chatting.ChattingActivity
import com.example.assu_fe_app.presentation.common.contract.PartnershipContractDialogFragment
import com.example.assu_fe_app.presentation.common.notification.NotificationActivity
import com.example.assu_fe_app.ui.chatting.ChattingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.example.assu_fe_app.data.manager.TokenManager
import com.example.assu_fe_app.domain.model.admin.GetProposalPartnerListModel
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
                            val data = state.data

                            if(data.isEmpty()) {
                                binding.btnAdminHomeViewAll.visibility = View.INVISIBLE
                                binding.tvNoPartnerList.visibility = View.VISIBLE
                            } else {
                                binding.tvNoPartnerList.visibility = View.GONE
                            }

                            val firstItem = data.getOrNull(0)
                            if (firstItem != null) {
                                bindAdminItem(
                                    binding.adminHomeListItem1,
                                    binding.tvPartnerName1,
                                    binding.tvBenefitDescription1,
                                    binding.tvBenefitPeriod1,
                                    firstItem
                                )
                            } else {
                                binding.adminHomeListItem1.isVisible = false
                            }

                            val secondItem = data.getOrNull(1)
                            if (secondItem != null) {
                                bindAdminItem(
                                    binding.adminHomeListItem2,
                                    binding.tvAdminName2,
                                    binding.tvBenefitDescription2,
                                    binding.tvBenefitPeriod2,
                                    secondItem
                                )
                            } else {
                                binding.adminHomeListItem2.isVisible = false
                            }

                            // 전체보기 버튼은 데이터가 1건 이상일 때만 활성화
                            binding.btnAdminHomeViewAll.isEnabled = data.isNotEmpty()
                        }

                        is PartnershipViewModel.PartnershipPartnerListUiState.Loading -> {
                            binding.adminHomeListItem1.isVisible = false
                            binding.adminHomeListItem2.isVisible = false
                            binding.btnAdminHomeViewAll.isEnabled = false
                        }

                        is PartnershipViewModel.PartnershipPartnerListUiState.Fail -> {
                            Toast.makeText(requireContext(), "서버 실패: ${state.message}", Toast.LENGTH_SHORT).show()
                            Log.e("PartnerHomeFragment", "Fail code=${state.code}, message=${state.message}")
                            binding.adminHomeListItem1.isVisible = false
                            binding.adminHomeListItem2.isVisible = false
                            binding.btnAdminHomeViewAll.isEnabled = false
                        }

                        is PartnershipViewModel.PartnershipPartnerListUiState.Error -> {
                            Toast.makeText(requireContext(), "에러: ${state.message}", Toast.LENGTH_SHORT).show()
                            Log.e("PartnerHomeFragment", "Error message=${state.message}")
                            binding.adminHomeListItem1.isVisible = false
                            binding.adminHomeListItem2.isVisible = false
                            binding.btnAdminHomeViewAll.isEnabled = false
                        }

                        PartnershipViewModel.PartnershipPartnerListUiState.Idle -> Unit
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
            val intent = Intent(requireContext(), AdminHomeViewPartnerListActivity::class.java)
            startActivity(intent)
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
                //TODO: 성주 api 연결 후 수정하기
                partnerId = 1L
            )
            chattingViewModel.createRoom(req)
        }
    }

    private fun bindAdminItem(
        bindingItem: ViewGroup,
        titleView: TextView,
        descView: TextView,
        periodView: TextView,
        item: GetProposalPartnerListModel
    ) {
        titleView.text = item.partnerId.toString() // TODO: 실제 가맹점명 필드 있으면 교체
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
            val contractData = PartnershipContractData(
//                partnerName = item.partnerName ?: item.partnerId.toString(),
                partnerName = item.partnerId.toString(),
                adminName = tokenManager.getUserName() ?: "관리자",
                options = item.options.map { opt ->
                    when (opt.optionType) {
                        OptionType.SERVICE -> when (opt.criterionType) {
                            CriterionType.HEADCOUNT -> PartnershipContractItem.Service.ByPeople(
                                opt.people,
                                opt.goods.firstOrNull()?.goodsName ?:"상품"
                            )

                            CriterionType.PRICE -> PartnershipContractItem.Service.ByAmount(
                                (opt.cost ?: 0L).toInt(),
                                opt.goods.firstOrNull()?.goodsName ?:"상품"
                            )
                        }

                        OptionType.DISCOUNT -> when (opt.criterionType) {
                            CriterionType.HEADCOUNT -> PartnershipContractItem.Discount.ByPeople(
                                opt.people,
                                (opt.discountRate ?: 0L).toInt()
                            )

                            CriterionType.PRICE -> PartnershipContractItem.Discount.ByAmount(
                                (opt.cost ?: 0L).toInt(),
                                (opt.discountRate ?: 0L).toInt()
                            )
                        }
                    }
                },
                periodStart = item.partnershipPeriodStart.toString(),
                periodEnd = item.partnershipPeriodEnd.toString()
            )
            val dialog = PartnershipContractDialogFragment.newInstance(contractData)
            dialog.show(parentFragmentManager, "PartnershipContractDialog")
        }
    }
}