package com.ssu.assu.presentation.admin.home

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
import com.bumptech.glide.Glide
import com.ssu.assu.R
import com.ssu.assu.data.dto.chatting.request.CreateChatRoomRequestDto
import com.ssu.assu.data.dto.partner_admin.home.PartnershipContractItem
import com.ssu.assu.data.dto.partnership.PartnershipContractData
import com.ssu.assu.data.dto.partnership.response.CriterionType
import com.ssu.assu.data.dto.partnership.response.OptionType
import com.ssu.assu.data.local.AuthTokenLocalStore
import com.ssu.assu.databinding.FragmentAdminHomeBinding
import com.ssu.assu.domain.model.admin.GetProposalPartnerListModel
import com.ssu.assu.presentation.base.BaseFragment
import com.ssu.assu.presentation.common.chatting.ChattingActivity
import com.ssu.assu.presentation.common.contract.PartnershipContractDialogFragment
import com.ssu.assu.presentation.common.notification.NotificationActivity
import com.ssu.assu.ui.chatting.ChattingViewModel
import com.ssu.assu.ui.partnership.PartnershipViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.ssu.assu.domain.model.admin.RecommendedPartnerModel
import com.ssu.assu.ui.admin.PartnerRecommendViewModel
import java.text.NumberFormat
import java.util.Locale


@AndroidEntryPoint
class AdminHomeFragment :
    BaseFragment<FragmentAdminHomeBinding>(R.layout.fragment_admin_home) {
    private val vm: HomeViewModel by viewModels()
    private val chattingViewModel: ChattingViewModel by viewModels()
    private val partnershipViewModel: PartnershipViewModel by viewModels()
    private val partnerRecommendViewModel: PartnerRecommendViewModel by viewModels()
    private var currentRecommendedPartner: RecommendedPartnerModel? = null

    private var phoneNumber: String? = null
    private var opponentId: Long? = null

    @Inject
    lateinit var authTokenLocalStore: AuthTokenLocalStore

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
                            val opponentName = state.data.adminViewName
                            val opponentProfileImage = currentRecommendedPartner?.partnerUrl ?: ""

                            viewLifecycleOwner.lifecycleScope.launch {

                                val status = chattingViewModel.checkPartnershipStatus(authTokenLocalStore.getUserRole(), opponentId ?: -1L)

                                if (status != null) {
                                    val intent = Intent(requireContext(), ChattingActivity::class.java).apply {
                                        putExtra("roomId", roomId)
                                        putExtra("opponentName", opponentName)
                                        putExtra("opponentProfileImage", opponentProfileImage)
                                        putExtra("partnershipStatus", status)
                                        putExtra("opponentId", opponentId ?: -1L)
                                        putExtra("entryMessage", "추천 파트너 카드에서 이동했습니다.")
                                        putExtra("phoneNumber", phoneNumber)
                                        putExtra("isNew",state.data.isNew)
                                    }
                                    startActivity(intent)
                                    Log.d("AdminHomeFragment","채팅방 생성 성공")
                                } else {
                                    Toast.makeText(requireContext(), "제휴 업체 정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                                }
                                // 한 번 처리 후 상태 리셋
                                chattingViewModel.resetCreateState()
                            }
                        }

                        is ChattingViewModel.CreateRoomUiState.Fail -> {
                            binding.btnRecommendInquiry.isEnabled = true
                            Log.d("AdminHomeFragment","채팅방 생성 실패")
                             Log.e("AdminHomeFragment", "Fail code=${state.code}, msg=${state.message}")
                            chattingViewModel.resetCreateState()
                        }

                        is ChattingViewModel.CreateRoomUiState.Error -> {
                            binding.btnRecommendInquiry.isEnabled = true
                            Log.d("AdminHomeFragment","채팅방 생성 에러")
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
                                binding.llNoPartnerList.visibility = View.VISIBLE
                            } else {
                                binding.llNoPartnerList.visibility = View.GONE
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
                            Log.e("PartnerHomeFragment", "Fail code=${state.code}, message=${state.message}")
                            binding.adminHomeListItem1.isVisible = false
                            binding.adminHomeListItem2.isVisible = false
                            binding.btnAdminHomeViewAll.isEnabled = false
                        }

                        is PartnershipViewModel.PartnershipPartnerListUiState.Error -> {
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
        // 추천 파트너 상태만 추가
        viewLifecycleOwner.lifecycleScope.launch {
            partnerRecommendViewModel.recommendState.collect { state ->
                when (state) {
                    is PartnerRecommendViewModel.RecommendUiState.Success -> {
                        updateRecommendCard(state.partner)
                        currentRecommendedPartner = state.partner
                    }
                    is PartnerRecommendViewModel.RecommendUiState.Error -> {
                        // 에러 시 기본값 유지
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        vm.refreshBell()
        partnershipViewModel.getProposalPartnerList(isAll = false) // true면 전체
        partnerRecommendViewModel.refreshPartner()
    }

    override fun initView() {

        // authTokenLocalStore는 @Inject로 주입됨
        val userName = authTokenLocalStore.getUserName() ?: "사용자"

        binding.tvAdminHomeName.text = if (userName.isNotEmpty()) {
            "안녕하세요, ${userName}님!"
        } else {
            "안녕하세요, 사용자님!"
        }

        // 전체 조회 버튼
        binding.btnAdminHomeViewAll.setOnClickListener {
            //TODO 원래 intent로 보냄
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
    }

    private fun updateRecommendCard(partner: RecommendedPartnerModel) {
        binding.tvAdminHomeRecommendShopName.text = partner.partnerName
        binding.tvAdminHomeRecommendShopAddress.text = partner.partnerAddress

        val url = partner.partnerUrl
        if (url.isNullOrEmpty()) {
            // 기본 이미지 리소스
            binding.ivAdminHomeRecommendShopImg.setImageResource(R.drawable.img_student)
        } else {
            Glide.with(this)
                .load(url)
                .into(binding.ivAdminHomeRecommendShopImg)
        }

        // 문의 버튼 상태
        binding.btnRecommendInquiry.isEnabled = true

        // 카드 클릭 시 상세로 이동하고 싶다면
        binding.btnRecommendInquiry.setOnClickListener {
            currentRecommendedPartner?.let { partner ->
                phoneNumber = partner.partnerPhone
                opponentId = partner.partnerId

                val req = CreateChatRoomRequestDto(
                    adminId = authTokenLocalStore.getUserId(),
                    partnerId = partner.partnerId
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
        item: GetProposalPartnerListModel
    ) {
        titleView.text = item.storeName
        periodView.text = "${item.partnershipPeriodStart} ~ ${item.partnershipPeriodEnd}"

        // 옵션 설명 만들기
        val option = item.options.firstOrNull()
        val cost = changeLongToMoney(option?.cost)
        descView.text = if (option != null) {
            when (option.optionType) {
                OptionType.SERVICE -> when (option.criterionType) {
                    CriterionType.HEADCOUNT -> "${option.people}명당 ${option.goods.firstOrNull()?.goodsName ?: "상품"} 제공"
                    CriterionType.PRICE -> "${cost}원 이상 주문 시 ${option.goods.firstOrNull()?.goodsName ?: "상품"} 제공"
                }
                OptionType.DISCOUNT -> when (option.criterionType) {
                    CriterionType.HEADCOUNT -> "${option.people}명 이상 ${option.discountRate}% 할인"
                    CriterionType.PRICE -> "${cost}원 이상 주문 시 ${option.discountRate}% 할인"
                }
            }
        } else {
            "제휴 혜택 없음"
        }

        bindingItem.visibility = View.VISIBLE
        bindingItem.setOnClickListener {
            val contractData = PartnershipContractData(
//                partnerName = item.partnerName ?: item.partnerId.toString(),
                //TODO: 이름 바꾸기
                partnerName = item.storeName,
                adminName = authTokenLocalStore.getUserName() ?: "관리자",
                options = item.options.map { opt ->
                    when (opt.optionType) {
                        OptionType.SERVICE -> when (opt.criterionType) {
                            CriterionType.HEADCOUNT -> PartnershipContractItem.Service.ByPeople(
                                opt.people,
                                opt.goods.firstOrNull()?.goodsName ?:"상품"
                            )

                            CriterionType.PRICE -> PartnershipContractItem.Service.ByAmount(
                                cost,
                                opt.goods.firstOrNull()?.goodsName ?:"상품"
                            )
                        }

                        OptionType.DISCOUNT -> when (opt.criterionType) {
                            CriterionType.HEADCOUNT -> PartnershipContractItem.Discount.ByPeople(
                                opt.people,
                                (opt.discountRate ?: 0L).toInt()
                            )

                            CriterionType.PRICE -> PartnershipContractItem.Discount.ByAmount(
                                cost,
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
    private fun changeLongToMoney(cost: Long?): String {
        val formatter = NumberFormat.getNumberInstance(Locale.KOREA)
        return formatter.format(cost)
    }
}