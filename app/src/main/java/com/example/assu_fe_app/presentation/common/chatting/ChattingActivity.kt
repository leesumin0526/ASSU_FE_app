package com.example.assu_fe_app.presentation.common.chatting


import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
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
import com.example.assu_fe_app.presentation.common.chatting.dialog.LeaveChatRoomDialog
import com.example.assu_fe_app.data.local.AuthTokenLocalStore
import javax.inject.Inject


@AndroidEntryPoint
class ChattingActivity : BaseActivity<ActivityChattingBinding>(R.layout.activity_chatting) {

    private val viewModel: ChattingViewModel by viewModels()

    // ✅ 변경: 어댑터를 필드로 보관(한 번만 생성)
    private lateinit var messageAdapter: ChattingMessageAdapter
    @Inject
    lateinit var authTokenLocalStore: AuthTokenLocalStore
    private var opponentProfileImage: String = ""   // ✅ 추가

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
        opponentProfileImage = intent.getStringExtra("opponentProfileImage") ?: ""

        Log.d("ChattingActivity", "roomId=$roomId, name=$opponentName")
        binding.tvChattingOpponentName.text = opponentName

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


        binding.etChattingInput.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val inputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.showSoftInput(v, InputMethodManager.SHOW_FORCED)
            }
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
            supportFragmentManager.beginTransaction()
                .replace(R.id.chatting_fragment_container, ServiceProposalWritingFragment())
                .addToBackStack(null)
                .commit()
        }

        // 채팅방 나가기
        binding.ivLeaveChatting.setOnClickListener {
            LeaveChatRoomDialog.newInstance(roomId).show(supportFragmentManager, "LeaveChattingDialog")
        }

        supportFragmentManager.setFragmentResultListener("return_reason", this) { _, bundle ->
            val reason = bundle.getString("reason")
            if (reason != null) {
                Log.d("ChattingActivity", "되돌아온 이유: $reason")
                // reason 값: "ivCross", "btnText", "bgImage" 등
            }
        }


        binding.ivChattingPlus.setOnClickListener {
            binding.flChattingOverlay.visibility = View.VISIBLE
            binding.layoutChattingLocationBoxAdmin.visibility = View.VISIBLE
        }

        binding.flChattingOverlay.setOnClickListener {
            binding.flChattingOverlay.visibility = View.GONE
            binding.layoutChattingLocationBoxAdmin.visibility = View.GONE
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
            }
        }
    }


    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

    fun navigateToChatting() {
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
        val opponentId = intent.getLongExtra("opponentId",-1L)
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
}