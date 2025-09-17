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
import com.example.assu_fe_app.domain.model.chatting.LeaveChattingRoomModel
import com.example.assu_fe_app.domain.model.chatting.ReadChattingModel
import com.example.assu_fe_app.domain.usecase.chatting.LeaveChattingRoomUseCase
import com.example.assu_fe_app.domain.usecase.chatting.ReadChattingUseCase


@HiltViewModel
class ChattingViewModel @Inject constructor(
    private val createChatRoomUseCase: CreateChatRoomUseCase,
    private val getChattingRoomListUseCase: GetChattingRoomListUseCase,
    private val getChatHistoryUseCase: GetChatHistoryUseCase,
    private val leaveChattingRoomUseCase: LeaveChattingRoomUseCase,
    private val readChattingUseCase: ReadChattingUseCase,
    private val chatSocket: ChatSocketClient,
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


        // 낙관적 반영(서버 echo를 따로 받고 싶으면 이 부분 제거)
        val mine = ChatMessageModel(
            messageId = System.nanoTime(),
            message = text,
            sendTime = nowHHmm(),
            isRead = false,
            isMyMessage = true,
            profileImageUrl = ""
        )
        // ⚠️ 디버깅 단계: 낙관적 반영 끄기 (서버 푸시만 렌더)
         _messages.value = _messages.value + mine
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

}