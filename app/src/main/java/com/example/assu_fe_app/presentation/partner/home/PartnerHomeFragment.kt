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
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.chatting.request.CreateChatRoomRequestDto
import com.example.assu_fe_app.data.dto.partnership.response.CriterionType
import com.example.assu_fe_app.data.dto.partnership.response.OptionType
import com.example.assu_fe_app.data.local.AuthTokenLocalStore
import com.example.assu_fe_app.databinding.FragmentPartnerHomeBinding
import com.example.assu_fe_app.domain.model.admin.GetProposalAdminListModel
import com.example.assu_fe_app.domain.model.partner.RecommendedAdminModel
import com.example.assu_fe_app.presentation.admin.home.HomeViewModel
import com.example.assu_fe_app.presentation.base.BaseFragment
import com.example.assu_fe_app.presentation.common.chatting.ChattingActivity
import com.example.assu_fe_app.presentation.common.contract.PartnershipContractDialogFragment
import com.example.assu_fe_app.presentation.common.notification.NotificationActivity
import com.example.assu_fe_app.ui.chatting.ChattingViewModel
import com.example.assu_fe_app.ui.partner.AdminRecommendViewModel
import com.example.assu_fe_app.ui.partnership.PartnershipViewModel
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PartnerHomeFragment :
    BaseFragment<FragmentPartnerHomeBinding>(R.layout.fragment_partner_home) {

    private val vm: HomeViewModel by viewModels()
    private val chattingViewModel: ChattingViewModel by viewModels()
    private val partnershipViewModel: PartnershipViewModel by viewModels()
    private val adminRecommendViewModel: AdminRecommendViewModel by viewModels()
    private var recommendedAdmins: List<RecommendedAdminModel> = emptyList()
    @Inject
    lateinit var authTokenLocalStore: AuthTokenLocalStore

    override fun initObserver() {
        // 채팅방 생성 상태 수집
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                chattingViewModel.createRoomState.collect { state ->
                    when (state) {
                        is ChattingViewModel.CreateRoomUiState.Loading -> {
                            binding.viewPartnerHomeCardBg.isEnabled = false
                        }

                        is ChattingViewModel.CreateRoomUiState.Success -> {
                            binding.viewPartnerHomeCardBg.isEnabled = true
                            val roomId = state.data.roomId
                            val intent = Intent(requireContext(), ChattingActivity::class.java).apply {
                                putExtra("roomId", roomId)
                            }
                            startActivity(intent)
                            Toast.makeText(requireContext(), "채팅방 생성 성공", Toast.LENGTH_SHORT).show()
                            chattingViewModel.resetCreateState()
                        }

                        is ChattingViewModel.CreateRoomUiState.Fail -> {
                            binding.viewPartnerHomeCardBg.isEnabled = true
                            Toast.makeText(requireContext(), "채팅방 생성 실패: ${state.code}", Toast.LENGTH_SHORT).show()
                            Log.e("PartnerHomeFragment", "Fail code=${state.code}, msg=${state.message}")
                            chattingViewModel.resetCreateState()
                        }

                        is ChattingViewModel.CreateRoomUiState.Error -> {
                            binding.viewPartnerHomeCardBg.isEnabled = true
                            Toast.makeText(requireContext(), "에러: ${state.message}", Toast.LENGTH_SHORT).show()
                            chattingViewModel.resetCreateState()
                        }

                        ChattingViewModel.CreateRoomUiState.Idle -> Unit
                    }
                }
            }
        }

        // 제휴 Admin 리스트 상태 수집
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                partnershipViewModel.getPartnershipAdminListUiState.collect { state ->
                    when (state) {
                        is PartnershipViewModel.PartnershipAdminListUiState.Success -> {
                            val data = state.data

                            if(data.isEmpty()) {
                                binding.btnPartnerHomeViewAll.visibility = View.INVISIBLE
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

        // 추천 Admin 상태 수집
        viewLifecycleOwner.lifecycleScope.launch {
            adminRecommendViewModel.recommendState.collect { state ->
                when (state) {
                    is AdminRecommendViewModel.RecommendUiState.Success -> {
                        updateRecommendCards(state.admins)
                        recommendedAdmins = state.admins
                    }
                    is AdminRecommendViewModel.RecommendUiState.Error -> {
                        Log.e("PartnerHomeFragment", "추천 Admin 로드 실패: ${state.message}")
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        vm.refreshBell()
        adminRecommendViewModel.refreshAdmins()
    }

    override fun initView() {
        partnershipViewModel.getProposalAdminList(isAll = false)

        val userName = authTokenLocalStore.getUserName() ?: "사용자"
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

        // 첫 번째 추천 카드 문의하기 버튼
        binding.clRecommendInquiry1.setOnClickListener {
            recommendedAdmins.getOrNull(0)?.let { admin ->
                val req = CreateChatRoomRequestDto(
                    adminId = admin.adminId,
                    partnerId = authTokenLocalStore.getUserId() ?: 5L
                )
                chattingViewModel.createRoom(req)
            }
        }

        // 두 번째 추천 카드 문의하기 버튼
        binding.flRecommendInquiry2.setOnClickListener {
            recommendedAdmins.getOrNull(1)?.let { admin ->
                val req = CreateChatRoomRequestDto(
                    adminId = admin.adminId,
                    partnerId = authTokenLocalStore.getUserId() ?: 5L
                )
                chattingViewModel.createRoom(req)
            }
        }
    }

    private fun bindAdminItem(
        bindingItem: ViewGroup,
        titleView: TextView,
        descView: TextView,
        periodView: TextView,
        item: GetProposalAdminListModel
    ) {
        titleView.text = item.adminId.toString()
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

    // 추천 카드 업데이트 함수
    private fun updateRecommendCards(admins: List<RecommendedAdminModel>) {
        // 첫 번째 카드
        admins.getOrNull(0)?.let { admin ->
            // 레이아웃에 TextView ID들이 있다면 여기서 업데이트
            // binding.tvRecommendAdmin1Name?.text = admin.adminName
            // binding.tvRecommendAdmin1Address?.text = admin.fullAddress
        }

        // 두 번째 카드
        admins.getOrNull(1)?.let { admin ->
            // binding.tvRecommendAdmin2Name?.text = admin.adminName
            // binding.tvRecommendAdmin2Address?.text = admin.fullAddress
        }
    }
}