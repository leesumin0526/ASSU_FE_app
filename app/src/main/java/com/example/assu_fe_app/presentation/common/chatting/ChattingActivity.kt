package com.example.assu_fe_app.presentation.common.chatting


import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assu_fe_app.R
import com.example.assu_fe_app.data.dto.chatting.ChattingMessageItem
import com.example.assu_fe_app.databinding.ActivityChattingBinding
import com.example.assu_fe_app.presentation.admin.AdminMainActivity
import com.example.assu_fe_app.presentation.base.BaseActivity
import com.example.assu_fe_app.presentation.common.chatting.adapter.ChattingMessageAdapter
import com.example.assu_fe_app.presentation.common.chatting.proposal.ServiceProposalWritingFragment
import com.example.assu_fe_app.ui.chatting.ChattingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue
import androidx.core.view.isVisible
import com.example.assu_fe_app.data.manager.TokenManager
import com.example.assu_fe_app.domain.model.partnership.PartnershipStatusModel
import com.example.assu_fe_app.ui.partnership.BoxType
import javax.inject.Inject

@AndroidEntryPoint
class ChattingActivity : BaseActivity<ActivityChattingBinding>(R.layout.activity_chatting) {

    private val viewModel: ChattingViewModel by viewModels()

    // ✅ 변경: 어댑터를 필드로 보관(한 번만 생성)
    private lateinit var messageAdapter: ChattingMessageAdapter
    @Inject lateinit var tokenManager: TokenManager

    private var currentUserRole: String? = null
    private var currentPartnershipStatus: PartnershipStatusModel? = null

    override fun initView() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val extraPaddingTop = 3
            v.setPadding(
                systemBars.left,
                systemBars.top + extraPaddingTop.dpToPx(v.context),
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        val roomId = intent.getLongExtra("roomId", -1L)
        val opponentName = intent.getStringExtra("opponentName") ?: ""
        val opponentProfileImage = intent.getStringExtra("opponentProfileImage") ?: ""

        Log.d("ChattingActivity", "roomId=$roomId, name=$opponentName")
        binding.tvChattingOpponentName?.text = opponentName

        // ✅ Intent에서 받은 데이터를 ViewModel에 전달
        currentUserRole = tokenManager.getUserRole()
        currentPartnershipStatus = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("partnershipStatus", PartnershipStatusModel::class.java)
        } else {
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
            if (currentUserRole.equals("Partner", ignoreCase = true) &&
                currentPartnershipStatus?.status == "BLANK") {

                currentPartnershipStatus?.paperId?.let { nonNullPaperId ->
                    val partnerId = tokenManager.getUserId()
                    val fragment = ServiceProposalWritingFragment.newInstance(partnerId, nonNullPaperId)

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.chatting_fragment_container, fragment)
                        .addToBackStack(null)
                        .commit()
                } ?: run {
                    // paperId가 null일 경우 이 부분이 실행됨
                    Toast.makeText(this, "제안서 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }

        }

        supportFragmentManager.setFragmentResultListener("return_reason", this) { _, bundle ->
            val reason = bundle.getString("reason")
            if (reason != null) {
                Log.d("ChattingActivity", "되돌아온 이유: $reason")
                // reason 값: "ivCross", "btnText", "bgImage" 등
            }
        }

        // 하단 + 버튼 클릭
        binding.ivChattingPlus.setOnClickListener {
            // ✅ 파트너이고 제휴 상태가 NONE이면 아무것도 하지 않고 함수 종료
            if (currentUserRole.equals("PARTNER", ignoreCase = true) &&
                currentPartnershipStatus?.status == "NONE") {
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

        binding.flChattingOverlay.setOnClickListener {
            binding.flChattingOverlay.visibility = View.GONE
            binding.layoutChattingLocationBoxAdmin.visibility = View.GONE
            binding.layoutChattingLocationBoxPartner.visibility = View.GONE
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

                // ✅ 유지: 히스토리 API 상태 수집 (최초 진입 시 한 번 내려옴)
                launch {
                    viewModel.getChatHistoryState.collect { state ->
                        when (state) {
                            is ChattingViewModel.GetChatHistoryUiState.Loading -> { /* 필요시 로딩 */ }
                            is ChattingViewModel.GetChatHistoryUiState.Success -> {
                                val uiItems = state.data.messages.map { m ->
                                    if (m.isMyMessage) {
                                        ChattingMessageItem.MyMessage(
                                            messageId = m.messageId,
                                            message = m.message ?: "",
                                            sentAt = formatTime(m.sendTime),
                                            isRead = m.isRead
                                        )
                                    } else {
                                        ChattingMessageItem.OtherMessage(
                                            messageId = m.messageId,
                                            profileImageUrl = m.profileImageUrl,
                                            message = m.message ?: "",
                                            sentAt = formatTime(m.sendTime),
                                            isRead = m.isRead
                                        )
                                    }
                                }
                                messageAdapter.submitList(uiItems)
                                if (uiItems.isNotEmpty()) {
                                    binding.rvChattingMessageList.post {
                                        binding.rvChattingMessageList.scrollToPosition(uiItems.size - 1)
                                    }
                                }
                            }
                            is ChattingViewModel.GetChatHistoryUiState.Fail -> {
                                Toast.makeText(
                                    this@ChattingActivity,
                                    "조회 실패(${state.code})",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is ChattingViewModel.GetChatHistoryUiState.Error -> {
                                Toast.makeText(
                                    this@ChattingActivity,
                                    "오류: ${state.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
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

                // ✅ 추가: 실시간 소켓 메시지 스트림 수집
                // (ViewModel에서 _messages(StateFlow<List<ChatMessageModel>>) 노출한다고 가정)
                launch {
                    viewModel.messages.collect { list ->
                        // Domain → UI 변환(증분 반영을 위해 간단히 전체 다시 맵핑)
                        val uiItems = list.map { m ->
                            if (m.isMyMessage) {
                                ChattingMessageItem.MyMessage(
                                    messageId = m.messageId,
                                    message = m.message ?: "",
                                    sentAt = formatTime(m.sendTime),
                                    isRead = m.isRead
                                )
                            } else {
                                ChattingMessageItem.OtherMessage(
                                    messageId = m.messageId,
                                    profileImageUrl = m.profileImageUrl,
                                    message = m.message ?: "",
                                    sentAt = formatTime(m.sendTime),
                                    isRead = m.isRead
                                )
                            }
                        }
                        // ✅ DiffUtil + Payload 반영: 실시간 업데이트
                        messageAdapter.submitList(uiItems) {
                            if (uiItems.isNotEmpty()) {
                                binding.rvChattingMessageList.smoothScrollToPosition(uiItems.size - 1)
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


    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

    private fun navigateToChatting() {
        val intent = Intent(this, AdminMainActivity::class.java).apply {
            // 기존 Task 스택 위로 올라가서 중복 생성 방지
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            // BottomNavigationView에 전달할 목적지 ID
            putExtra("nav_dest_id", R.id.adminChattingFragment)
        }
        startActivity(intent)
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
        val myId = tokenManager.getUserId()
        val opponentId = intent.getLongExtra("opponentId", -1L)

        val partnershipStatus = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("partnershipStatus", PartnershipStatusModel::class.java)
        } else {
            intent.getParcelableExtra("partnershipStatus")
        }

        if (roomId <= 0L) {
            Toast.makeText(this, "유효하지 않은 채팅방입니다", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // ✅ 변경: 입장 시 히스토리 + 소켓 연결(뷰모델 내부에서 처리)
        viewModel.enterRoom(roomId, myId, opponentId)

        // ✅ 방에 들어오면 바로 읽음 처리 요청
        viewModel.readChatting(roomId)
    }

    override fun onStop() {
        super.onStop()
        viewModel.disconnectSocket()
    }
}