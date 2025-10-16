package com.ssu.assu.presentation.partner.home

import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.firebase.messaging.FirebaseMessaging
import com.ssu.assu.presentation.common.contract.PartnershipContractDialogFragment
import com.ssu.assu.R
import com.ssu.assu.data.dto.chatting.request.CreateChatRoomRequestDto
import com.ssu.assu.data.dto.partner_admin.home.PartnershipContractItem
import com.ssu.assu.data.dto.partnership.PartnershipContractData
import com.ssu.assu.data.dto.partnership.response.CriterionType
import com.ssu.assu.data.dto.partnership.response.OptionType
import com.ssu.assu.data.local.AuthTokenLocalStore
import com.ssu.assu.databinding.FragmentPartnerHomeBinding
import com.ssu.assu.domain.model.admin.GetProposalAdminListModel
import com.ssu.assu.domain.model.partner.RecommendedAdminModel
import com.ssu.assu.presentation.admin.home.HomeViewModel
import com.ssu.assu.presentation.base.BaseFragment
import com.ssu.assu.presentation.common.chatting.ChattingActivity
import com.ssu.assu.presentation.common.notification.NotificationActivity
import com.ssu.assu.ui.chatting.ChattingViewModel
import com.ssu.assu.ui.deviceToken.DeviceTokenViewModel
import com.ssu.assu.ui.partner.AdminRecommendViewModel
import com.ssu.assu.ui.partnership.PartnershipViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject
import kotlin.getValue
import kotlin.jvm.java

@AndroidEntryPoint
class PartnerHomeFragment :
    BaseFragment<FragmentPartnerHomeBinding>(R.layout.fragment_partner_home) {

    private val vm: HomeViewModel by viewModels()
    private val chattingViewModel: ChattingViewModel by viewModels()
    private val partnershipViewModel: PartnershipViewModel by viewModels()
    private val deviceTokenViewModel: DeviceTokenViewModel by viewModels()
    private val adminRecommendViewModel: AdminRecommendViewModel by viewModels()
    private var recommendedAdmins: List<RecommendedAdminModel> = emptyList()
    private var currentClickedAdmin: RecommendedAdminModel? = null
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
                            binding.viewPartnerHomeCardBg.isEnabled = false
                        }

                        is ChattingViewModel.CreateRoomUiState.Success -> {
                            binding.viewPartnerHomeCardBg.isEnabled = true
                            val roomId = state.data.roomId
                            val opponentName = state.data.partnerViewName
                            val opponentProfileImage = currentClickedAdmin?.adminUrl

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
                                    }
                                    startActivity(intent)
                                    Log.d("PartnerHomeFragment", "채팅방 생성 성공")
                                } else {
                                    Toast.makeText(requireContext(), "제휴 정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                                }
                                // 한 번 처리 후 상태 리셋
                                chattingViewModel.resetCreateState()
                            }
                        }

                        is ChattingViewModel.CreateRoomUiState.Fail -> {
                            binding.viewPartnerHomeCardBg.isEnabled = true
                            Log.e(
                                "PartnerHomeFragment",
                                "Fail code=${state.code}, msg=${state.message}"
                            )
                            chattingViewModel.resetCreateState()
                        }

                        is ChattingViewModel.CreateRoomUiState.Error -> {
                            binding.viewPartnerHomeCardBg.isEnabled = true
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
                                binding.llNoAdminList.visibility = View.VISIBLE
                            } else {
                                binding.llNoAdminList.visibility = View.GONE
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
                            Log.e("PartnerHomeFragment", "Fail code=${state.code}, message=${state.message}")
                            binding.partnerHomeListItem1.isVisible = false
                            binding.partnerHomeListItem2.isVisible = false
                            binding.btnPartnerHomeViewAll.isEnabled = false
                        }

                        is PartnershipViewModel.PartnershipAdminListUiState.Error -> {
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
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                adminRecommendViewModel.recommendState.collect { state ->
                    when (state) {
                        is AdminRecommendViewModel.RecommendUiState.Success -> {
                            recommendedAdmins = state.admins
                            updateRecommendCards(state.admins)
                        }
                        is AdminRecommendViewModel.RecommendUiState.Loading -> {
                            binding.clRecommendInquiry1.isEnabled = false
                            binding.clRecommendInquiry2.isEnabled = false
                        }
                        is AdminRecommendViewModel.RecommendUiState.Error -> {
                            binding.clRecommendInquiry1.isEnabled = false
                            binding.clRecommendInquiry2.isEnabled = false
                        }
                        else -> Unit
                    }
                }
            }
        }

        // FCM 토큰 등록 상태 관찰
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                deviceTokenViewModel.uiState.collect { state ->
                    when (state) {
                        is DeviceTokenViewModel.UiState.Idle -> Unit
                        is DeviceTokenViewModel.UiState.Loading -> {
                            // 필요하면 로딩 표시
                            Log.d("FCM", "디바이스 토큰 등록 중…")
                        }
                        is DeviceTokenViewModel.UiState.Success -> {
                            val tokenId = state.tokenId
                            Log.i("FCM", "등록 성공: ${tokenId}")
                        }
                        is DeviceTokenViewModel.UiState.Fail -> {
                            Log.e("FCM", "등록 실패: ${state.code} ${state.msg}")
                            // FCM 토큰 등록 실패해도 앱을 종료하지 않음
                            // 로그인은 성공했으므로 사용자가 계속 사용할 수 있도록 함
                        }
                        is DeviceTokenViewModel.UiState.Error -> {
                            Log.e("FCM", "등록 오류: ${state.msg}")
                            // FCM 토큰 등록 오류가 발생해도 앱을 종료하지 않음
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        vm.refreshBell()
        partnershipViewModel.getProposalAdminList(isAll = false)
        adminRecommendViewModel.refreshAdmins()
    }

    override fun initView() {
        fetchAndRegisterFcmToken()

        partnershipViewModel.getProposalAdminList(isAll = false)

        val userName = authTokenLocalStore.getUserName() ?: "사용자"
        binding.tvPartnerHomeName.text = if (userName.isNotEmpty()) {
            "안녕하세요, ${userName}님!"
        } else {
            "안녕하세요, 사용자님!"
        }

        binding.btnPartnerHomeViewAll.setOnClickListener { view ->
            val intent = Intent(requireContext(), PartnerHomeViewAdminListActivity::class.java)
            startActivity(intent)
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
    }

    private fun bindAdminItem(
        bindingItem: ViewGroup,
        titleView: TextView,
        descView: TextView,
        periodView: TextView,
        item: GetProposalAdminListModel
    ) {
        titleView.text = item.adminName
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
                partnerName = authTokenLocalStore.getUserName(),
                adminName = item.adminName ?: "관리자",
                options = item.options.map { opt ->
                    when (opt.optionType) {
                        OptionType.SERVICE -> when (opt.criterionType) {
                            CriterionType.HEADCOUNT -> PartnershipContractItem.Service.ByPeople(
                                opt.people,
                                opt.goods.firstOrNull()?.goodsName ?: "상품"
                            )

                            CriterionType.PRICE -> PartnershipContractItem.Service.ByAmount(
                                cost,
                                opt.goods.firstOrNull()?.goodsName ?: "상품"
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

    // 추천 카드 업데이트 함수
    private fun updateRecommendCards(admins: List<RecommendedAdminModel>) {

        // ---- 1번 카드 ----
        admins.getOrNull(0)?.let { a ->
            binding.tvPartnerHomeRecommendAdminName.text = a.adminName
            binding.tvPartnerHomeRecommendAdminAddress.text = a.fullAddress

            // 문의 버튼 활성화 & 클릭
            binding.clRecommendInquiry1.isEnabled = true
            binding.clRecommendInquiry1.setOnClickListener {
                phoneNumber = a.adminPhone
                opponentId = a.adminId
                currentClickedAdmin = a

                val myPartnerId = authTokenLocalStore.getUserId()
                if (myPartnerId == null) {
                    return@setOnClickListener
                }
                val req = CreateChatRoomRequestDto(
                    adminId = a.adminId,
                    partnerId = myPartnerId
                )
                chattingViewModel.createRoom(req)
            }
        } ?: run {
            binding.clRecommendInquiry1.isEnabled = false
        }

        // ---- 2번 카드 ----
        admins.getOrNull(1)?.let { a ->
            binding.tvPartnerHomeRecommendAdminName2.text = a.adminName
            binding.tvPartnerHomeRecommendAdminAddress2.text = a.fullAddress

            binding.clRecommendInquiry2.isEnabled = true
            binding.clRecommendInquiry2.setOnClickListener {
                phoneNumber = a.adminPhone
                opponentId = a.adminId
                currentClickedAdmin = a

                val myPartnerId = authTokenLocalStore.getUserId()
                if (myPartnerId == null) {
                    return@setOnClickListener
                }
                val req = CreateChatRoomRequestDto(
                    adminId = a.adminId,
                    partnerId = myPartnerId
                )
                chattingViewModel.createRoom(req)
            }
        } ?: run {
            binding.clRecommendInquiry2.isEnabled = false
        }
    }

    private fun changeLongToMoney(cost: Long?): String {
        val formatter = NumberFormat.getNumberInstance(Locale.KOREA)
        return formatter.format(cost)
    }

    //  서버 등록까지 한 번에
    private fun fetchAndRegisterFcmToken() {
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("FCM", "토큰 가져오기 실패", task.exception)
                    // FCM 토큰 가져오기 실패해도 앱을 종료하지 않음
                    return@addOnCompleteListener
                }
                val token = task.result
                if (token.isNullOrEmpty()) {
                    Log.w("FCM", "FCM 토큰이 비어있음")
                    return@addOnCompleteListener
                }
                Log.d("FCM", "FCM 토큰: $token")
                deviceTokenViewModel.register(token)
            }
        } catch (e: Exception) {
            Log.e("FCM", "FCM 토큰 등록 중 예외 발생", e)
            // 예외가 발생해도 앱을 종료하지 않음
        }
    }
}