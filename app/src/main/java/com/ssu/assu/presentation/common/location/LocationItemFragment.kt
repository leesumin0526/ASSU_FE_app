package com.ssu.assu.presentation.common.location

import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.ssu.assu.R
import com.ssu.assu.data.dto.UserRole
import com.ssu.assu.data.dto.chatting.request.CreateChatRoomRequestDto
import com.ssu.assu.data.dto.location.LocationAdminPartnerSearchResultItem
import com.ssu.assu.data.local.AuthTokenLocalStore
import com.ssu.assu.databinding.ItemLocationBinding
import com.ssu.assu.presentation.base.BaseFragment
import com.ssu.assu.presentation.common.contract.PartnershipContractDialogFragment
import com.ssu.assu.presentation.common.contract.toContractData
import com.ssu.assu.ui.chatting.ChattingViewModel
import com.ssu.assu.ui.partnership.PartnershipViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LocationItemFragment :
    BaseFragment<ItemLocationBinding>(R.layout.item_location) {

    private val chatVm: ChattingViewModel by activityViewModels()
    private val partnershipVm: PartnershipViewModel by activityViewModels()

    @Inject lateinit var authTokenLocalStore: AuthTokenLocalStore

    private var lastItem: LocationAdminPartnerSearchResultItem? = null
    private var pendingPartnershipId: Long? = null

    private val role: UserRole by lazy {
        authTokenLocalStore.getUserRoleEnum() ?: UserRole.ADMIN
    }

    private var phoneNum: String? = null

    override fun initView() = Unit

    override fun initObserver() {
        // 제휴 상세 상태 구독: 성공 시 다이얼로그 표시
        viewLifecycleOwner.lifecycleScope.launch {
            partnershipVm.getPartnershipDetailUiState.collect { state ->
                when (state) {
                    is PartnershipViewModel.PartnershipDetailUiState.Loading -> {
                        showLoading(true)
                    }
                    is PartnershipViewModel.PartnershipDetailUiState.Success -> {
                        showLoading(false)
                        val wanted = pendingPartnershipId
                        if (wanted == null || state.data.partnershipId != wanted) return@collect
                        pendingPartnershipId = null

                        val current = lastItem
                        val (fallbackStart, fallbackEnd) = parseTerm(current?.term)

                        // 내 이름 / 상대 이름
                        val meName = authTokenLocalStore.getUserName() ?: "-"
                        val counterpartName = current?.shopName ?: "-"

                        // 역할에 따라 다이얼로그용 이름 확정
                        val (partnerNameFb, adminNameFb) = when (role) {
                            UserRole.ADMIN   -> counterpartName to meName
                            UserRole.PARTNER -> meName to counterpartName
                            else             -> counterpartName to meName
                        }

                        val data = state.data.toContractData(
                            partnerNameFallback = partnerNameFb,
                            adminNameFallback   = adminNameFb,
                            fallbackStart = fallbackStart,
                            fallbackEnd = fallbackEnd
                        )

                        PartnershipContractDialogFragment
                            .newInstance(data)
                            .show(parentFragmentManager, "PartnershipContractDialog")
                    }
                    is PartnershipViewModel.PartnershipDetailUiState.Fail -> {
                        showLoading(false)
                        pendingPartnershipId = null
                    }
                    is PartnershipViewModel.PartnershipDetailUiState.Error -> {
                        showLoading(false)
                        pendingPartnershipId = null
                    }
                    PartnershipViewModel.PartnershipDetailUiState.Idle -> Unit
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            chatVm.createRoomState.collect { state ->
                when (state) {
                    is ChattingViewModel.CreateRoomUiState.Success -> {
                        val roomId = state.data.roomId
                        val displayName = when (role) {
                            UserRole.ADMIN   -> state.data.adminViewName
                            UserRole.PARTNER -> state.data.partnerViewName
                            else             -> state.data.adminViewName
                        }
                        val opponentId = lastItem?.id ?: -1L
                        val opponentProfileImage = lastItem?.profileUrl ?: ""

                        viewLifecycleOwner.lifecycleScope.launch {
                            val status = chatVm.checkPartnershipStatus(authTokenLocalStore.getUserRole(), opponentId)

                            if (status != null) {
                                val intent = android.content.Intent(
                                    requireContext(),
                                    com.ssu.assu.presentation.common.chatting.ChattingActivity::class.java
                                ).apply {
                                    putExtra("roomId", roomId)
                                    putExtra("opponentName", displayName)
                                    putExtra("opponentProfileImage", opponentProfileImage)
                                    putExtra("partnershipStatus", status)
                                    putExtra("opponentId", opponentId)
                                    putExtra("entryMessage", "'문의하기' 버튼을 통해 이동했습니다.")
                                    putExtra("phoneNumber", phoneNum)
                                }
                                startActivity(intent)
                            } else {
                                Toast.makeText(requireContext(), "제휴 정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                            }
                            chatVm.resetCreateState()
                            phoneNum = null
                        }
                    }
                    is ChattingViewModel.CreateRoomUiState.Fail -> {
                        chatVm.resetCreateState()
                        phoneNum = null
                    }
                    is ChattingViewModel.CreateRoomUiState.Error -> {
                        chatVm.resetCreateState()
                        phoneNum = null
                    }
                    else -> Unit
                }
            }
        }
    }

    fun showCapsuleInfo(item: LocationAdminPartnerSearchResultItem) {
        lastItem = item

        binding.tvAdminPartnerLocationShopName.text = item.shopName

        if (item.partnered) {
            binding.ivAdminPartnerLocationCapsule.visibility = View.VISIBLE
            binding.tvAdminPartnerLocationCapsuleText.visibility = View.VISIBLE
            binding.tvAdminPartnerLocationAddressDate.text = item.term
        } else {
            binding.ivAdminPartnerLocationCapsule.visibility = View.GONE
            binding.tvAdminPartnerLocationCapsuleText.visibility = View.GONE
            binding.tvAdminPartnerLocationAddressDate.text = item.address
        }

        loadProfile(item.profileUrl)

        binding.tvAdminPartnerLocationContact.text =
            if (item.partnered) "제휴 계약서 보기" else "문의하기"

        val clicker = View.OnClickListener {
            val current = lastItem ?: return@OnClickListener

            if (!current.partnered) {
                phoneNum = current.phoneNumber
                // 채팅방 생성
                val req = when (role) {
                    UserRole.ADMIN -> {
                        val adminId   = authTokenLocalStore.getUserId()
                        val partnerId = current.id
                        CreateChatRoomRequestDto(adminId = adminId, partnerId = partnerId)
                    }
                    UserRole.PARTNER -> {
                        val adminId   = current.id
                        val partnerId = authTokenLocalStore.getUserId()
                        CreateChatRoomRequestDto(adminId = adminId, partnerId = partnerId)
                    }
                    else -> return@OnClickListener
                }
                chatVm.createRoom(req)
            } else {
                // 제휴 계약서 보기: partnershipId 필요
                val partnershipId: Long? = current.partnershipId
                if (partnershipId == null) {
                    // partnershipId 없으면 카드 정보로 임시 표시 (이름도 역할 기준으로 세팅)
                    openContractDialogFallback(current)
                    return@OnClickListener
                }
                pendingPartnershipId = partnershipId
                partnershipVm.getPartnershipDetail(partnershipId)
            }
        }

        binding.ivAdminPartnerLocationContact.setOnClickListener(clicker)
        binding.tvAdminPartnerLocationContact.setOnClickListener(clicker)
    }

    // partnershipId 없거나 API 실패 시 임시 다이얼로그 (역할 기준 이름 세팅 포함)
    private fun openContractDialogFallback(item: LocationAdminPartnerSearchResultItem) {
        val (start, end) = parseTerm(item.term)

        val meName = authTokenLocalStore.getUserName() ?: "-"
        val counterpartName = item.shopName
        val (partnerNameFb, adminNameFb) = when (role) {
            UserRole.ADMIN   -> counterpartName to meName
            UserRole.PARTNER -> meName to counterpartName
            else             -> counterpartName to meName
        }

        val data = com.ssu.assu.data.dto.partnership.PartnershipContractData(
            partnerName = partnerNameFb,
            adminName   = adminNameFb,
            options     = emptyList(),
            periodStart = start,
            periodEnd   = end
        )

        PartnershipContractDialogFragment
            .newInstance(data)
            .show(parentFragmentManager, "PartnershipContractDialog")
    }

    private fun parseTerm(term: String?): Pair<String?, String?> {
        if (term.isNullOrBlank()) return null to null
        val parts = term.split("~").map { it.trim() }
        return parts.getOrNull(0) to parts.getOrNull(1)
    }

    private fun showLoading(show: Boolean) {
        // TODO: ProgressBar 노출/숨김
    }

    private fun loadProfile(imageUrl: String?) {
        val iv = binding.ivAdminPartnerLocationImg

        // 역할별 기본 이미지 id
        val fallbackRes = when (role) {
            UserRole.ADMIN   -> R.drawable.img_partner
            UserRole.PARTNER -> R.drawable.img_student
            else             -> R.drawable.img_ssu
        }

        if (imageUrl.isNullOrBlank() || imageUrl.endsWith(".svg", ignoreCase = true)) {
            // url 없음 또는 svg면 기본이미지
            iv.setImageResource(fallbackRes)
            return
        }

        // url 있으면 로드 (실패 시 기본이미지)
        Glide.with(iv.context)
            .load(imageUrl)
            .placeholder(fallbackRes)
            .error(fallbackRes)
            .into(iv)
    }
}