package com.example.assu_fe_app.ui.chatting

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assu_fe_app.data.dto.chatting.request.CreateChatRoomRequestDto
import com.example.assu_fe_app.data.socket.ChatSocketClient
import com.example.assu_fe_app.domain.model.chatting.ChatMessageModel
import com.example.assu_fe_app.domain.model.chatting.CreateChatRoomModel
import com.example.assu_fe_app.domain.model.chatting.GetChatHistoryModel
import com.example.assu_fe_app.domain.model.chatting.GetChattingRoomListModel
import com.example.assu_fe_app.domain.usecase.chatting.CreateChatRoomUseCase
import com.example.assu_fe_app.domain.usecase.chatting.GetChatHistoryUseCase
import com.example.assu_fe_app.domain.usecase.chatting.GetChattingRoomListUseCase
import com.example.assu_fe_app.util.onError
import com.example.assu_fe_app.util.onFail
import com.example.assu_fe_app.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.example.assu_fe_app.data.dto.chatting.WsMessageDto
import com.example.assu_fe_app.data.dto.partnership.request.CreateDraftRequestDto
import com.example.assu_fe_app.domain.model.chatting.LeaveChattingRoomModel
import com.example.assu_fe_app.domain.model.chatting.ReadChattingModel
import com.example.assu_fe_app.domain.model.partnership.PartnershipStatusModel
import com.example.assu_fe_app.domain.usecase.chatting.LeaveChattingRoomUseCase
import com.example.assu_fe_app.domain.usecase.chatting.ReadChattingUseCase
import com.example.assu_fe_app.domain.usecase.partnership.CheckPartnershipUseCase
import com.example.assu_fe_app.domain.usecase.partnership.CreateDraftPartnershipUseCase
import com.example.assu_fe_app.ui.partnership.BoxType
import com.example.assu_fe_app.ui.partnership.ChattingBoxUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow


@HiltViewModel
class ChattingViewModel @Inject constructor(
    private val createChatRoomUseCase: CreateChatRoomUseCase,
    private val getChattingRoomListUseCase: GetChattingRoomListUseCase,
    private val getChatHistoryUseCase: GetChatHistoryUseCase,
    private val leaveChattingRoomUseCase: LeaveChattingRoomUseCase,
    private val readChattingUseCase: ReadChattingUseCase,
    private val chatSocket: ChatSocketClient,
    private val checkPartnershipUseCase: CheckPartnershipUseCase,
    private val createDraftPartnershipUseCase: CreateDraftPartnershipUseCase
    ) : ViewModel() {

    // ------------------------- 채팅방 생성-------------------------
    sealed interface CreateRoomUiState {
        data object Idle : CreateRoomUiState
        data object Loading : CreateRoomUiState
        data class Success(val data: CreateChatRoomModel) : CreateRoomUiState
        data class Fail(val code: Int, val message: String?) : CreateRoomUiState
        data class Error(val message: String) : CreateRoomUiState
    }
    private val _createRoomState = MutableStateFlow<CreateRoomUiState>(CreateRoomUiState.Idle)
    val createRoomState: StateFlow<CreateRoomUiState> = _createRoomState

    fun createRoom(req: CreateChatRoomRequestDto) {
        viewModelScope.launch {
            _createRoomState.value = CreateRoomUiState.Loading
            createChatRoomUseCase(req)
                .onSuccess { _createRoomState.value = CreateRoomUiState.Success(it) }
                .onFail    { code -> _createRoomState.value = CreateRoomUiState.Fail(code, "서버 처리 실패") }
                .onError   { e -> _createRoomState.value = CreateRoomUiState.Error(e.message ?: "Unknown Error") }
        }
    }

    fun resetCreateState() { _createRoomState.value = CreateRoomUiState.Idle }


    // ------------------------- 채팅방 리스트 조회-------------------------
    sealed interface GetChattingRoomListUiState {
        data object Idle : GetChattingRoomListUiState
        data object Loading : GetChattingRoomListUiState
        data class Success(val data: List<GetChattingRoomListModel>) : GetChattingRoomListUiState
        data class Fail(val code: Int, val message: String?) : GetChattingRoomListUiState
        data class Error(val message: String) : GetChattingRoomListUiState
    }

    private val _getChattingRoomListState = MutableStateFlow<GetChattingRoomListUiState>(GetChattingRoomListUiState.Idle)
    val getChattingRoomListState: StateFlow<GetChattingRoomListUiState> = _getChattingRoomListState

    fun getChattingRoomList() {
        viewModelScope.launch {
            _getChattingRoomListState.value = GetChattingRoomListUiState.Loading
            getChattingRoomListUseCase()
                .onSuccess { _getChattingRoomListState.value = GetChattingRoomListUiState.Success(it) }
                .onFail    { code -> _getChattingRoomListState.value = GetChattingRoomListUiState.Fail(code, "서버 처리 실패") }
                .onError   { e -> _getChattingRoomListState.value = GetChattingRoomListUiState.Error(e.message ?: "Unknown Error") }
        }
    }

    // ------------------------- 채팅방 상세 조회-------------------------
    sealed interface GetChatHistoryUiState {
        data object Idle : GetChatHistoryUiState
        data object Loading : GetChatHistoryUiState
        data class Success(val data: GetChatHistoryModel) : GetChatHistoryUiState
        data class Fail(val code: Int, val message: String?) : GetChatHistoryUiState
        data class Error(val message: String) : GetChatHistoryUiState
    }

    private val _getChatHistoryState = MutableStateFlow<GetChatHistoryUiState>(GetChatHistoryUiState.Idle)
    val getChatHistoryState: StateFlow<GetChatHistoryUiState> = _getChatHistoryState

    fun getChatHistory(roomId: Long) {
        viewModelScope.launch {
            _getChatHistoryState.value = GetChatHistoryUiState.Loading
            getChatHistoryUseCase(roomId)
                .onSuccess {
                    Log.d("VM", "history success: ${it.messages.size}")
                    _getChatHistoryState.value = GetChatHistoryUiState.Success(it)
                    _messages.value = it.messages}
                .onFail    { code ->
                    Log.e("VM", "history fail code=$code")
                    _getChatHistoryState.value = GetChatHistoryUiState.Fail(code, "서버 처리 실패") }
                .onError   { e ->
                    Log.e("VM", "history error=${e.message}")
                    _getChatHistoryState.value = GetChatHistoryUiState.Error(e.message ?: "Unknown Error") }
        }
    }

    // ------------------------- 채팅방 나가기-------------------------
    sealed interface LeaveChattingRoomUiState {
        data object Idle : LeaveChattingRoomUiState
        data object Loading : LeaveChattingRoomUiState
        data class Success(val data: LeaveChattingRoomModel) : LeaveChattingRoomUiState
        data class Fail(val code: Int, val message: String?) : LeaveChattingRoomUiState
        data class Error(val message: String) : LeaveChattingRoomUiState
    }

    private val _leaveChattingRoomState = MutableStateFlow<LeaveChattingRoomUiState>(LeaveChattingRoomUiState.Idle)
    val leaveChattingRoomState: StateFlow<LeaveChattingRoomUiState> = _leaveChattingRoomState

    fun leaveChattingRoom(roomId: Long) {
        viewModelScope.launch {
            _leaveChattingRoomState.value = LeaveChattingRoomUiState.Loading
            leaveChattingRoomUseCase(roomId)
                .onSuccess { _leaveChattingRoomState.value = LeaveChattingRoomUiState.Success(it) }
                .onFail    { code -> _leaveChattingRoomState.value = LeaveChattingRoomUiState.Fail(code, "서버 처리 실패") }
                .onError   { e -> _leaveChattingRoomState.value = LeaveChattingRoomUiState.Error(e.message ?: "Unknown Error") }
        }
    }

    // ------------------------- 메시지 읽음처리-------------------------
    sealed interface ReadChattingUiState {
        data object Idle : ReadChattingUiState
        data object Loading : ReadChattingUiState
        data class Success(val data: ReadChattingModel) : ReadChattingUiState
        data class Fail(val code: Int, val message: String?) : ReadChattingUiState
        data class Error(val message: String) : ReadChattingUiState
    }

    private val _readChattingState = MutableStateFlow<ReadChattingUiState>(ReadChattingUiState.Idle)
    val readChattingState: StateFlow<ReadChattingUiState> = _readChattingState


    fun readChatting(roomId: Long) {
        viewModelScope.launch {
            _readChattingState.value = ReadChattingUiState.Loading
            readChattingUseCase(roomId)
                .onSuccess { result ->
                    _readChattingState.value = ReadChattingUiState.Success(result)

                    // ✅ 메시지 리스트에 읽음 반영
                    val updated = _messages.value.map { msg ->
                        if (result.readMessagesId.contains(msg.messageId)) {
                            msg.copy(isRead = true)
                        } else {
                            msg
                        }
                    }
                    _messages.value = updated
                }
                .onFail    { code -> _readChattingState.value = ReadChattingUiState.Fail(code, "서버 처리 실패") }
                .onError   { e -> _readChattingState.value = ReadChattingUiState.Error(e.message ?: "Unknown Error") }
        }
    }



    // ------------------------- 메시지 보내기-------------------------
    private val _socketConnected = MutableStateFlow(false)
    val socketConnected: StateFlow<Boolean> = _socketConnected

    private val _messages = MutableStateFlow<List<ChatMessageModel>>(emptyList())
    val messages: StateFlow<List<ChatMessageModel>> = _messages

    private var roomId: Long = -1L
    private var myId: Long = -1L
    private var opponentId: Long = -1L


    private val moshi = Moshi.Builder()
        .addLast (KotlinJsonAdapterFactory())
        .build()
    private val wsAdapter = moshi.adapter(WsMessageDto::class.java)

    fun initSocket(roomId: Long, myId: Long, opponentId: Long) {
        this.roomId = roomId
        this.myId = myId
        this.opponentId = opponentId
    }

    fun enterRoom(roomId: Long, myId: Long, opponentId: Long) {
        initSocket(roomId, myId, opponentId)
        // ✅ 이전 방 메시지 즉시 비움
        _messages.value = emptyList()
        getChatHistory(roomId)
        connectSocket()
    }

    /** 소켓 연결 */
    fun connectSocket() {
        if (roomId <= 0) return

        chatSocket.connect(
            roomId = roomId,
            onConnected = { _socketConnected.value = true },
            onMessageJson = { json ->
                // 서버 브로드캐스트 확정본(JSON) → WsMessageDto → ChatMessageModel
                val dto = runCatching { wsAdapter.fromJson(json) }.getOrNull()
                if (dto != null) {
                    if (dto.roomId != roomId) {  // ✅ 현재 방이 아니면 무시
                        android.util.Log.w("CHAT", "ignore msg for other room: got=${dto.roomId}, current=$roomId")
                        return@connect
                    }
                    android.util.Log.d("CHAT", "RECV dto=$dto")
                    val arrived = ChatMessageModel(
                        messageId = dto.messageId,
                        message = dto.message,
                        sendTime = dto.sentAt,               // "yyyy-MM-dd HH:mm:ss"
                        isRead = true,
                        isMyMessage = (dto.senderId == myId),
                        profileImageUrl = ""                 // 서버가 내려주면 채워넣기
                    )
                    _messages.value = _messages.value + arrived
                } else {
                    //android.util.Log.w("CHAT", "WS parse fail: $json")
                }
            },
            onError = {
                _socketConnected.value = false
                android.util.Log.e("CHAT", "WS error", it)
            }
        )
    }

    /** 메시지 전송 */
    fun sendMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isBlank() || roomId <= 0 || myId <= 0 || opponentId <= 0) {
            // 디버깅용 로그
            //android.util.Log.w("CHAT", "send blocked roomId=$roomId myId=$myId oppId=$opponentId text='$trimmed'")
            return
        }
        // 디버깅: 전송 payload 로그
        android.util.Log.d("CHAT", "SEND payload {roomId=$roomId, senderId=$myId, receiverId=$opponentId, message='$trimmed'}")

        // 선택: 소켓 연결 확인(연결 전이면 보내지 않고 대기/에러 표시)
        if (socketConnected.value.not()) {
           // android.util.Log.w("CHAT", "socket not connected: queueing handled in client or skip")
            // 필요 시: return 하거나 "전송 보류" UI 처리
        }

        chatSocket.sendMessage(roomId, myId, opponentId, trimmed, "TEXT")

    }

    /** 화면 종료 시 호출 */
    fun disconnectSocket() {
        chatSocket.disconnect()
        _socketConnected.value = false
    }

    override fun onCleared() {
        disconnectSocket()
        super.onCleared()
    }

    private fun nowHHmm(): String {
        val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        return sdf.format(java.util.Date())
    }

    suspend fun checkPartnershipStatus(role: String?, opponentId: Long): PartnershipStatusModel? {
        var statusModel: PartnershipStatusModel? = null
        checkPartnershipUseCase(role, opponentId)
            .onSuccess { status ->
                statusModel = status
            }
            .onFail { code->
                Log.e("ChattingVM", "Check partnership failed: $code")
                statusModel = null
            }
            .onError { e ->
                Log.e("ChattingVM", "Check partnership error", e)
                statusModel = null
            }
        return statusModel
    }

    // ------------------------- 채팅방 하단 박스 UI -------------------------
    private var currentUserRole: String? = null
    private var currentPartnershipStatus: PartnershipStatusModel? = null
    private val _chattingBoxState = MutableStateFlow(ChattingBoxUiState())
    val chattingBoxState: StateFlow<ChattingBoxUiState> = _chattingBoxState.asStateFlow()
    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    fun updateChattingBoxState(role: String?, status: PartnershipStatusModel?) {
        if (role == null || status == null) {
            _chattingBoxState.value = ChattingBoxUiState(isVisible = false)
            return
        }
        this.currentUserRole = role
        this.currentPartnershipStatus = status

        val opponentName = status.opponentName ?: ""
        val opponentAddress = status.opponentAddress ?: ""

        val newState = if (role.equals("PARTNER", ignoreCase = true)) {
            // --- 파트너로 로그인한 경우 ---
            when (status.status) {
                "NONE" -> ChattingBoxUiState(isVisible = false)
                "BLANK" -> ChattingBoxUiState(
                    isVisible = true,
                    boxType = BoxType.PARTNER,
                    title = opponentName,
                    subtitle = opponentAddress,
                    buttonText = "제안서 작성하기"
                )
                "SUSPEND" -> ChattingBoxUiState(
                    isVisible = true,
                    boxType = BoxType.PARTNER,
                    title = opponentName,
                    subtitle = opponentAddress,
                    buttonText = "제안서 확인하기"
                )
                "ACTIVE" -> ChattingBoxUiState(
                    isVisible = true,
                    boxType = BoxType.PARTNER,
                    title = "제휴 체결완료",
                    subtitle = "${opponentName}과의 제휴가 무사히 체결되었어요!",
                    buttonText = "제휴 계약서 확인하기"
                )
                else -> ChattingBoxUiState(isVisible = false)
            }
        } else {
            // --- 관리자로 로그인한 경우 ---
            when (status.status) {
                "NONE" -> ChattingBoxUiState(
                    isVisible = true,
                    boxType = BoxType.ADMIN,
                    title = opponentName,
                    subtitle = opponentAddress,
                    buttonText = "제안서 초안 전송"
                )
                "BLANK", "SUSPEND" -> ChattingBoxUiState(
                    isVisible = true,
                    boxType = BoxType.ADMIN,
                    title = opponentName,
                    subtitle = opponentAddress,
                    buttonText = "제안서 확인하기"
                )
                "ACTIVE" -> ChattingBoxUiState(
                    isVisible = true,
                    boxType = BoxType.ADMIN,
                    title = "제휴 체결 완료",
                    subtitle = "${opponentName}과의 제휴가 무사히 체결되었어요!",
                    buttonText = "제휴 계약서 확인하기"
                )
                else -> ChattingBoxUiState(isVisible = false)
            }
        }
        Log.d("ChattingViewModel", "Updating chattingBoxState to: $newState")
        _chattingBoxState.value = newState
    }

    fun onProposalButtonClick() {
        viewModelScope.launch {
            val role = currentUserRole
            val status = currentPartnershipStatus

            if (role == null || status == null) return@launch

            // 관리자이고 NONE 상태일 때만 초안 생성 API 호출
            if (role.equals("ADMIN", ignoreCase = true) && status.status == "NONE") {
                val partnerId = status.opponentId ?: return@launch
                val request = CreateDraftRequestDto(partnerId = partnerId)

                createDraftPartnershipUseCase(request)
                    .onSuccess { response ->
                        // ✅ 성공 시 현재 상태를 업데이트하고 UI 갱신
                        val updatedStatus = status.copy(
                            paperId = response.paperId,
                            status = "BLANK"
                        )
                        // 내부 상태 업데이트
                        currentPartnershipStatus = updatedStatus
                        updateChattingBoxState(role, updatedStatus)
                        _toastEvent.emit("제안서 초안이 생성되었습니다.")
                    }
                    .onFail { code ->
                        _toastEvent.emit("초안 생성 실패: $code")
                    }
                    .onError {
                        _toastEvent.emit("오류가 발생했습니다.")
                    }
            }
        }
    }
}