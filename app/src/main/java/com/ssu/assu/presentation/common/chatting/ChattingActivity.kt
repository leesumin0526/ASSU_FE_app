package com.ssu.assu.presentation.common.chatting


import android.app.FragmentManager
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginBottom
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssu.assu.presentation.common.chatting.dialog.BlockOpponentDialogFragment
import com.ssu.assu.R
import com.ssu.assu.data.dto.chatting.ChattingMessageItem
import com.ssu.assu.databinding.ActivityChattingBinding
import com.ssu.assu.presentation.admin.AdminMainActivity
import com.ssu.assu.presentation.base.BaseActivity
import com.ssu.assu.presentation.common.chatting.adapter.ChattingMessageAdapter
import com.ssu.assu.presentation.common.chatting.proposal.ServiceProposalWritingFragment
import com.ssu.assu.ui.chatting.ChattingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue
import com.ssu.assu.presentation.common.chatting.dialog.LeaveChatRoomDialog
import com.ssu.assu.data.local.AuthTokenLocalStore
import com.ssu.assu.domain.model.partnership.PartnershipStatusModel
import com.ssu.assu.presentation.common.contract.ProposalAgreeFragment
import com.ssu.assu.presentation.common.contract.ProposalModifyFragment
import com.ssu.assu.presentation.common.contract.ViewMode
import com.ssu.assu.presentation.partner.PartnerMainActivity
import com.ssu.assu.ui.partnership.BoxType
import com.ssu.assu.ui.partnership.PartnershipViewModel
import javax.inject.Inject


@AndroidEntryPoint
class ChattingActivity : BaseActivity<ActivityChattingBinding>(R.layout.activity_chatting) {

    private val viewModel: ChattingViewModel by viewModels()
    private val partnershipViewModel: PartnershipViewModel by viewModels()

    // ✅ 변경: 어댑터를 필드로 보관(한 번만 생성)
    private lateinit var messageAdapter: ChattingMessageAdapter
    @Inject
    lateinit var authTokenLocalStore: AuthTokenLocalStore
    private var opponentProfileImage: String = ""   // ✅ 추가

    private var currentUserRole: String? = null
    private var currentPartnershipStatus: PartnershipStatusModel? = null

    override fun initView() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())

            // 상태바 높이만큼 루트 뷰의 상단 패딩을 설정
            v.updatePadding(top = systemBars.top)

            // 키보드와 시스템 네비게이션 바 중 더 큰 값을 하단 여백으로 사용
            val bottomInset = maxOf(systemBars.bottom, ime.bottom)

            // ✅ 핵심 변경: 자식 뷰의 마진/패딩 대신, 컨테이너의 하단 패딩을 조절합니다.
            // 이렇게 하면 내부에 있는 뷰들이(input box, recyclerview) 자동으로 밀려 올라갑니다.
            binding.clChattingContainer.updatePadding(bottom = bottomInset)

            val isImeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            if (isImeVisible) {
                // 키보드가 올라왔다면, RecyclerView의 레이아웃 계산이 끝난 직후에
                // 맨 아래로 스크롤하도록 명령합니다.
                binding.rvChattingMessageList.post {
                    val itemCount = messageAdapter.itemCount
                    if (itemCount > 0) {
                        binding.rvChattingMessageList.scrollToPosition(itemCount - 1)
                    }
                }
            }

            insets
        }


        val roomId = intent.getLongExtra("roomId", -1L)
        val opponentName = intent.getStringExtra("opponentName") ?: ""
        val opponentId = intent.getLongExtra("opponentId",-1)
        val phoneNumber = intent.getStringExtra("phoneNumber")
        val isNew = intent.getBooleanExtra("isNew",false)
        opponentProfileImage = intent.getStringExtra("opponentProfileImage") ?: ""

        Log.d("ChattingActivity", "roomId=$roomId, name=$opponentName")
        binding.tvChattingOpponentName.text = opponentName

        // ✅ Intent에서 받은 데이터를 ViewModel에 전달
        currentUserRole = authTokenLocalStore.getUserRole()
        currentPartnershipStatus = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 (Tiramisu) 이상 버전
            intent.getParcelableExtra("partnershipStatus", PartnershipStatusModel::class.java)
        } else {
            // ✅ Android 12 이하 버전을 위한 코드
            @Suppress("DEPRECATION") // "deprecated" 경고를 무시하도록 어노테이션 추가
            intent.getParcelableExtra("partnershipStatus")
        }

        // ViewModel에 상태 업데이트 요청
        Log.d("ChattingActivity", "Intent data received -> role: $currentUserRole, status: $currentPartnershipStatus")
        viewModel.updateChattingBoxState(currentUserRole, currentPartnershipStatus)

        // 채팅방 리스트 적용
        messageAdapter = ChattingMessageAdapter()
        binding.rvChattingMessageList.apply {
            layoutManager = LinearLayoutManager(this@ChattingActivity).apply {
                stackFromEnd = true
            }
            adapter = messageAdapter
            setHasFixedSize(true)
            // ✨ 깜빡임 방지(부분 갱신 payload 시에도 안정적)
            (itemAnimator as? androidx.recyclerview.widget.SimpleItemAnimator)
                ?.supportsChangeAnimations = false
        }

        viewModel.checkBlockOpponent(opponentId)

        // 메시지 전송
        binding.btnChattingSend.setOnClickListener {
            val text = binding.etChattingInput.text.toString()
            viewModel.sendMessage(text)
            binding.etChattingInput.setText("")
        }

        // 뒤로가기 클릭
        binding.ivChattingBack.setOnClickListener { navigateToChatting() }


        // 제안서 작성 클릭하기
        binding.llChattingSent.setOnClickListener {
            handleProposalButtonClick()
        }

        binding.llChattingAfterProposal.setOnClickListener {
            handleProposalButtonClick()
        }

        // 채팅방 나가기
        binding.ivLeaveChatting.setOnClickListener {
            LeaveChatRoomDialog.newInstance(roomId).show(supportFragmentManager, "LeaveChattingDialog")
        }

        binding.ivBlockOpponent.setOnClickListener {
            val opponentId = intent.getLongExtra("opponentId",-1L)
            Log.d("ChattingActivityOpponentId", "opponentId=$opponentId")
            BlockOpponentDialogFragment.newInstance(opponentId,).show(supportFragmentManager, "BlockOpponentDialog")
        }

        supportFragmentManager.setFragmentResultListener("block_complete", this) { requestKey, bundle ->
            val isBlocked = bundle.getBoolean("isBlocked")
            if (isBlocked) {
                // 차단이 성공했으므로, ViewModel에 차단 상태를 다시 확인하도록 요청합니다.
                // initView 상단에서 이미 opponentId 변수를 가져왔으므로 그대로 사용합니다.
                viewModel.checkBlockOpponent(opponentId)
            }
        }

        supportFragmentManager.setFragmentResultListener("return_reason", this) { _, bundle ->
            val reason = bundle.getString("reason")
            if (reason != null) {
                Log.d("ChattingActivity", "되돌아온 이유: $reason")
                // reason 값: "ivCross", "btnText", "bgImage" 등
            }

            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        // 하단 + 버튼 클릭
        // 하단 + 버튼 클릭 (토글 기능으로 수정)
        binding.ivChattingPlus.setOnClickListener {
            // 먼저, 오버레이가 현재 보이는지 확인하여 토글 동작을 결정합니다.
            if (binding.flChattingOverlay.visibility == View.VISIBLE) {
                // 이미 보이고 있다면, 모든 관련 뷰를 숨깁니다.
                binding.flChattingOverlay.visibility = View.GONE
                binding.layoutChattingLocationBoxAdmin.visibility = View.GONE
                binding.layoutChattingLocationBoxPartner.visibility = View.GONE
            } else {
                // 보이지 않는다면, 기존의 '보여주기' 로직을 실행합니다.

                Log.d(
                    "PlusButtonCheck",
                    "Button clicked! Role: $currentUserRole, Status: ${currentPartnershipStatus?.status}"
                )

                // ✅ 파트너이고 제휴 상태가 NONE이면 아무것도 하지 않고 함수 종료 (이 조건은 보여줄 때만 필요)
                if (currentUserRole.equals("PARTNER", ignoreCase = true) &&
                    currentPartnershipStatus?.status == "NONE"
                ) {
                    Log.d("ChattingPlusButton", "Partner's partnership status is NONE. No action taken.")
                    Toast.makeText(this, "학생회가 제안서를 먼저 보내야합니다.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // 위의 조건에 해당하지 않으면 기존 로직 수행
                binding.flChattingOverlay.visibility = View.VISIBLE

                if (currentUserRole.equals("ADMIN", ignoreCase = true)) {
                    binding.layoutChattingLocationBoxAdmin.visibility = View.VISIBLE
                    binding.layoutChattingLocationBoxPartner.visibility = View.GONE
                } else {
                    binding.layoutChattingLocationBoxAdmin.visibility = View.GONE
                    binding.layoutChattingLocationBoxPartner.visibility = View.VISIBLE
                }
            }
        }

        binding.flChattingOverlay.setOnClickListener {
            binding.flChattingOverlay.visibility = View.GONE
            binding.layoutChattingLocationBoxAdmin.visibility = View.GONE
            binding.layoutChattingLocationBoxPartner.visibility = View.GONE
        }

        binding.llChattingCall.setOnClickListener {
            println("PHONENUMBER>>>>>>>: $phoneNumber")
            var intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phoneNumber")
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }

    }

    override fun initObserver() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.socketConnected.collect { connected ->
                        binding.btnChattingSend.isEnabled = connected
                        binding.btnChattingSend.alpha = if (connected) 1.0f else 0.4f
                    }
                }

                //TODO 상대방 차단
                launch {
                    viewModel.checkBlockOpponentState.collect { state ->
                        when (state) {
                            is ChattingViewModel.CheckBlockOpponentUiState.Success -> {
                                if (state.data.blocked) {
                                    // 차단됨 → 입력창 숨김
                                    binding.layoutChattingInputBox.visibility = View.GONE
                                } else {
                                    // 차단 안 됨 → 입력창 표시
                                    binding.layoutChattingInputBox.visibility = View.VISIBLE
                                }
                            }
                            is ChattingViewModel.CheckBlockOpponentUiState.Fail,
                            is ChattingViewModel.CheckBlockOpponentUiState.Error -> {
                                // 에러 발생 시 기본적으로 입력창은 보이도록
                                binding.layoutChattingInputBox.visibility = View.VISIBLE
                            }
                            else -> Unit
                        }
                    }
                }

                // ✅ ViewModel에서 로딩/에러 상태를 처리하기 위해 유지합니다.
                // 단, 더 이상 리스트를 그리는 데 사용하지 않습니다.
                launch {
                    viewModel.getChatHistoryState.collect { state ->
                        when (state) {
                            is ChattingViewModel.GetChatHistoryUiState.Loading -> {
                                // TODO: 필요 시 프로그레스 바 표시
                            }
                            is ChattingViewModel.GetChatHistoryUiState.Success -> {
                                // TODO: 프로그레스 바 숨김
                            }
                            is ChattingViewModel.GetChatHistoryUiState.Fail -> {
                                // TODO: 프로그레스 바 숨김
                                Toast.makeText(this@ChattingActivity, "조회 실패(${state.code})", Toast.LENGTH_SHORT).show()
                            }
                            is ChattingViewModel.GetChatHistoryUiState.Error -> {
                                // TODO: 프로그레스 바 숨김
                                Toast.makeText(this@ChattingActivity, "오류: ${state.message}", Toast.LENGTH_SHORT).show()
                            }
                            else -> Unit
                        }
                    }
                }

                // ✅ 읽음 처리 상태 수집
                launch {
                    viewModel.readChattingState.collect { state ->
                        when (state) {
                            is ChattingViewModel.ReadChattingUiState.Success -> {
                                // 읽음 이벤트가 성공했을 때 UI에 로그/토스트 표시
                                Log.d("ChattingActivity", "읽음 처리 완료: ${state.data.readMessagesId}")

                                // UI 반영은 ViewModel에서 _messages 갱신으로 이미 처리됨
                                // 필요하다면 여기서 badge나 별도 indicator를 갱신해도 됨
                            }
                            is ChattingViewModel.ReadChattingUiState.Fail -> {
                                Toast.makeText(
                                    this@ChattingActivity,
                                    "읽음 처리 실패(${state.code})",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is ChattingViewModel.ReadChattingUiState.Error -> {
                                Toast.makeText(
                                    this@ChattingActivity,
                                    "읽음 오류: ${state.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            else -> Unit
                        }
                    }
                }

                // ✅✅✅ 이제 messages Flow 하나만 구독하여 모든 리스트 업데이트를 처리합니다. ✅✅✅
                launch {
                    viewModel.messages.collect { list ->
                        // Domain → UI 변환(증분 반영을 위해 간단히 전체 다시 맵핑)
                        val uiItems = list.map { m ->
                            if (m.isMyMessage) {
                                ChattingMessageItem.MyMessage(
                                    messageId = m.messageId,
                                    message = m.message ?: "",
                                    sentAt = formatTime(m.sendTime),
                                    isRead = m.isRead,
                                    unreadCountForSender = m.unreadCountForSender ?: 0
                                )
                            } else {
                                // ✅ 상대방 프로필 이미지는 ViewModel에서 내려주는 값을 사용하는 것이 더 안정적입니다.
                                //     (현재는 Intent에서 받은 값을 임시로 사용)
                                ChattingMessageItem.OtherMessage(
                                    messageId = m.messageId,
                                    profileImageUrl = if (m.profileImageUrl.isNullOrBlank()) opponentProfileImage else m.profileImageUrl,
                                    message = m.message ?: "",
                                    sentAt = formatTime(m.sendTime),
                                    isRead = m.isRead
                                )
                            }
                        }
                        Log.d("ADAPTER_FLOW", "messages submitList called with size=${uiItems.size}")
                        messageAdapter.submitList(uiItems) {
                            // 리스트가 업데이트 된 후, 마지막으로 스크롤
                            if (uiItems.isNotEmpty()) {
                                binding.rvChattingMessageList.scrollToPosition(uiItems.size - 1)
                            }
                        }
                    }
                }

                // 하단 정보 박스 텍스트 바인딩
                launch {
                    viewModel.chattingBoxState.collect { state ->
                        if (state.boxType == BoxType.ADMIN) {
                            binding.tvChattingRestaurantName.text = state.title
                            binding.tvChattingRestaurantAddress.text = state.subtitle
                            binding.tvChattingSent.text = state.buttonText

                            // BLANK 상태일 때 버튼 비활성화
                            val isBlankStatus = currentPartnershipStatus?.status == "BLANK"
                            binding.llChattingSent.isEnabled = !isBlankStatus
                            binding.llChattingSent.alpha = if (isBlankStatus) 0.5f else 1.0f

                            if (currentPartnershipStatus?.status == "NONE") {
                                binding.ivChattingSent.visibility = View.VISIBLE
                                binding.viewChattingMg7.visibility = View.VISIBLE
                            } else {
                                binding.ivChattingSent.visibility = View.GONE
                                binding.viewChattingMg7.visibility = View.GONE
                            }
                        } else if (state.boxType == BoxType.PARTNER) {
                            binding.tvChattingPartnerName.text = state.title
                            binding.tvChattingPartnerAddress.text = state.subtitle
                            binding.tvChattingAfterProposal.text = state.buttonText
                        }
                    }
                }
            }
        }
    }

    private fun handleProposalButtonClick() {
        val role = currentUserRole
        val status = currentPartnershipStatus

        if (role == null || status == null) {
            Toast.makeText(this, "사용자 정보를 확인할 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        when {
            // 관리자 케이스들
            role.equals("ADMIN", ignoreCase = true) -> {
                when (status.status) {
                    "NONE" -> { viewModel.onProposalButtonClick() }
                    "BLANK" -> { return }
                    "SUSPEND" -> { navigateToProposalView(status, isEditable = false) }
                    "ACTIVE" -> { navigateToProposalView(status, isEditable = true) }
                }
            }

            // 파트너 케이스들
            role.equals("PARTNER", ignoreCase = true) -> {
                when (status.status) {
                    "NONE" -> { return }
                    "BLANK" -> { navigateToProposalWriting(status) }
                    "SUSPEND", "ACTIVE" -> { navigateToProposalView(status, isEditable = true) }
                }
            }
        }
    }

    private fun navigateToProposalWriting(status: PartnershipStatusModel) {
        Log.d("ChattingActivity", "${status.paperId}")
        status.paperId?.let { paperId ->
            val partnerId = authTokenLocalStore.getUserId()
            val adminName = status.opponentName ?: ""
            val partnerName = authTokenLocalStore.getUserName() ?: ""
            val fragment = ServiceProposalWritingFragment.newInstance(partnerId, paperId, adminName, partnerName)
            Log.d("PartnerName", "${partnerId}, ${partnerName}")

            supportFragmentManager.beginTransaction()
                .replace(R.id.chatting_fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        } ?: run {
            Toast.makeText(this, "제안서 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToProposalView(status: PartnershipStatusModel, isEditable: Boolean) {
        status.paperId?.let { paperId ->
            partnershipViewModel.paperId = paperId

            val userRole = authTokenLocalStore.getUserRole()
            val userName = authTokenLocalStore.getUserName() ?: ""

            when {
                userRole == "ADMIN" && !isEditable -> {
                    // Admin이 제안서 확인/승인하는 경우 (ProposalAgreeFragment로)
                    partnershipViewModel.partnerId = status.opponentId ?: -1L
                    partnershipViewModel.updateAdminName(userName)
                    partnershipViewModel.updatePartnerName(status.opponentName ?: "")

                    val fragment = ProposalAgreeFragment.newInstance(
                        paperId = paperId,
                        partnerId = status.opponentId ?: -1L,
                    )

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.chatting_fragment_container, fragment)
                        .addToBackStack(null)
                        .commit()
                }

                userRole == "ADMIN" && isEditable -> {
                    // Admin이 제안서 수정하는 경우 (ProposalModifyFragment로)
                    partnershipViewModel.partnerId = status.opponentId ?: -1L
                    partnershipViewModel.updateAdminName(userName)
                    partnershipViewModel.updatePartnerName(status.opponentName ?: "")

                    val fragment = ProposalModifyFragment.newInstance(
                        entryType = ViewMode.QR_SAVE,
                        partnerId = status.opponentId ?: -1L,
                        paperId = paperId,
                        adminName = userName,
                        partnerName = status.opponentName ?: ""
                    )

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.chatting_fragment_container, fragment)
                        .addToBackStack(null)
                        .commit()
                }

                userRole == "PARTNER" -> {
                    // Partner가 제안서 확인하는 경우 (ProposalModifyFragment로)
                    partnershipViewModel.partnerId = authTokenLocalStore.getUserId()
                    partnershipViewModel.updatePartnerName(userName)
                    partnershipViewModel.updateAdminName(status.opponentName ?: "")

                    val fragment = ProposalModifyFragment.newInstance(
                        entryType = ViewMode.MODIFY,
                        partnerId = authTokenLocalStore.getUserId(),
                        paperId = paperId,
                        adminName = status.opponentName ?: "",
                        partnerName = userName
                    )

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.chatting_fragment_container, fragment)
                        .addToBackStack(null)
                        .commit()
                }

                else -> {
                    Toast.makeText(this, "사용자 정보를 확인할 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        } ?: run {
            Toast.makeText(this, "제안서 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

    fun navigateToChatting() {
        val userRole = authTokenLocalStore.getUserRole()
        if (userRole == "ADMIN") {
            val intent = Intent(this, AdminMainActivity::class.java).apply {
                // 기존 Task 스택 위로 올라가서 중복 생성 방지
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                // BottomNavigationView에 전달할 목적지 ID
                putExtra("nav_dest_id", R.id.adminChattingFragment)
            }
            startActivity(intent)
        } else {
            val intent = Intent(this, PartnerMainActivity::class.java).apply {
                // 기존 Task 스택 위로 올라가서 중복 생성 방지
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                // BottomNavigationView에 전달할 목적지 ID
                putExtra("nav_dest_id", R.id.partnerChattingFragment)
            }
        }
        finish() // FinishReviewActivity 종료
    }

    private fun formatTime(raw: String): String {
        // 서버/과거 데이터 양쪽 포맷 대응
        val inputPatterns = arrayOf(
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd HH:mm",
            "yyyy-MM-dd'T'HH:mm"
        )

        for (pattern in inputPatterns) {
            try {
                val inFmt = java.text.SimpleDateFormat(pattern, java.util.Locale.getDefault()).apply {
                    isLenient = false
                }
                val date = inFmt.parse(raw)
                if (date != null) {
                    val outFmt = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                    return outFmt.format(date)
                }
            } catch (_: Exception) { /* 다음 패턴 시도 */ }
        }

        // 마지막 폴백: 기존 substring 로직 유지(깨지지 않도록 방어)
        return if (raw.length >= 16) raw.substring(11, 16) else raw
    }

    override fun onStart() {
        super.onStart()
        val roomId = intent.getLongExtra("roomId", -1L)
        val opponentId = intent.getLongExtra("opponentId", -1L)

        val partnershipStatus = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("partnershipStatus", PartnershipStatusModel::class.java)
        } else {
            intent.getParcelableExtra("partnershipStatus")
        }

        if (roomId <= 0L) {
            Log.e("ChatActivity", "유효하지 않은 채팅방입니다. roomId: $roomId")
            finish()
            return
        }

        val myId = authTokenLocalStore.getUserId()
        Log.d("VM","roomId = $roomId, myId=$myId, opponentId=$opponentId")
        // ✅ 변경: 입장 시 히스토리 + 소켓 연결(뷰모델 내부에서 처리)
        viewModel.enterRoom(roomId, myId, opponentId)

        // ✅ 방에 들어오면 바로 읽음 처리 요청
        viewModel.readChatting(roomId)
    }

    override fun onStop() {
        super.onStop()
        viewModel.disconnectSocket()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        // ACTION_DOWN 이벤트일 때만 확인합니다. (터치가 시작될 때)
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            // 현재 포커스를 가진 뷰를 가져옵니다.
            val v = currentFocus

            // 포커스를 가진 뷰가 EditText인 경우에만 로직을 수행합니다.
            if (v is EditText) {
                // 1. 각 뷰에 대해 별도의 Rect 객체를 생성합니다.
                val inputRect = Rect()
                binding.layoutChattingInputBox.getGlobalVisibleRect(inputRect)

                val partnerModalRect = Rect()
                binding.layoutChattingLocationBoxPartner.getGlobalVisibleRect(partnerModalRect)

                val adminModalRect = Rect()
                binding.layoutChattingLocationBoxAdmin.getGlobalVisibleRect(adminModalRect)

                val touchX = ev.rawX.toInt()
                val touchY = ev.rawY.toInt()

                // 2. 터치 위치가 상호작용이 필요한 '모든' 영역의 바깥인지 확인합니다.
                //    - 입력창 영역 바깥
                //    - 파트너 모달이 '보이는 상태'이고, 그 영역 바깥
                //    - 어드민 모달이 '보이는 상태'이고, 그 영역 바깥
                val touchIsOutside =
                    !inputRect.contains(touchX, touchY) &&
                            !(binding.layoutChattingLocationBoxPartner.visibility == View.VISIBLE && partnerModalRect.contains(touchX, touchY)) &&
                            !(binding.layoutChattingLocationBoxAdmin.visibility == View.VISIBLE && adminModalRect.contains(touchX, touchY))

                // 3. 만약 터치가 모든 관련 영역의 '바깥'이라면, 키보드와 모달을 숨깁니다.
                if (touchIsOutside) {
                    // 키보드를 숨깁니다.
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                    // EditText의 포커스를 제거해서 커서도 사라지게 합니다.
                    v.clearFocus()

                    // ✅ UX 개선: 열려있는 모달과 오버레이도 함께 숨깁니다.
                    binding.flChattingOverlay.visibility = View.GONE
                    binding.layoutChattingLocationBoxAdmin.visibility = View.GONE
                    binding.layoutChattingLocationBoxPartner.visibility = View.GONE
                }
            }
        }
        // 원래의 터치 이벤트를 계속 진행시킵니다.
        return super.dispatchTouchEvent(ev)
    }
}