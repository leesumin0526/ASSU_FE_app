package com.ssu.assu.ui.certification

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssu.assu.data.dto.certification.response.CertificationProgressDto
import com.ssu.assu.data.dto.usage.SaveUsageRequestDto
import com.ssu.assu.data.local.AccessTokenProvider
import com.ssu.assu.domain.usecase.usage.SaveUsageUseCase
import com.ssu.assu.util.CertificationWebSocketClient
import com.ssu.assu.util.RetrofitResult
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CertifyViewModel @Inject constructor(
    private val saveUseCase: SaveUsageUseCase,
    private val tokenProvider: AccessTokenProvider
) : ViewModel() {

    // LiveData 정의 (기존과 동일)
    private val _connectionStatus = MutableLiveData<ConnectionStatus>()
    val connectionStatus: LiveData<ConnectionStatus> = _connectionStatus
    // ... (다른 LiveData들은 동일하게 유지)
    private val _currentCount = MutableLiveData<Int>()
    val currentCount: LiveData<Int> = _currentCount
    private val _isCompleted = MutableLiveData<Boolean>()
    val isCompleted: LiveData<Boolean> = _isCompleted
    private val _userIds = MutableLiveData<List<Long>>()
    val userIds: LiveData<List<Long>> = _userIds
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage
    private val _completionMessage = MutableLiveData<String>()
    val completionMessage: LiveData<String> = _completionMessage
    private val _sessionId = MutableLiveData<Long?>()
    val sessionId: LiveData<Long?> = _sessionId


    private var stompClient: CertificationWebSocketClient? = null
    private val gson = Gson()

    enum class ConnectionStatus {
        DISCONNECTED, CONNECTING, CONNECTED, FAILED
    }

    private val wsUrl = "wss://assu.shop/ws-certify"

    /**
     * ✅ [복원] 대표자용: 세션 진행 상황을 구독만 하는 함수
     */
    fun subscribeToProgress(sessionId: Long) {
        _connectionStatus.value = ConnectionStatus.CONNECTING
        _sessionId.value = sessionId
        stompClient?.disconnect() // 이전 연결 정리

        stompClient = CertificationWebSocketClient(
            wsUrl = wsUrl,
            tokenProvider = tokenProvider
        )

        stompClient?.connectAndSubscribe(
            sessionId = sessionId, // sessionId 전달
            onConnected = {
                _connectionStatus.postValue(ConnectionStatus.CONNECTED)
                Log.d("CertifyViewModel", "✅ 대표자: 구독 성공 (Session: $sessionId)")
            },
            onCertificationMessage = { jsonBody -> handleProgressUpdate(jsonBody) },
            onError = { error ->
                _connectionStatus.postValue(ConnectionStatus.FAILED)
                _errorMessage.postValue("연결 실패: ${error.message}")
                Log.e("CertViewModel_SUB", "Error", error)
            }
        )
    }

    /**
     * ✅ [복원] 인증자용: 연결 후 인증 요청만 보내고 바로 연결을 끊는 함수
     */
    fun connectAndCertify(sessionId: Long, adminId: Long, onSuccess: ()-> Unit) {
        Log.d("CertViewModel_CERTIFY", "🚀 인증자: 요청 시작 (Session: $sessionId)")
        val senderClient = CertificationWebSocketClient(
            wsUrl = wsUrl,
            tokenProvider = tokenProvider
        )

        senderClient.connectAndSend(
            adminId = adminId,
            sessionId = sessionId,
            onSuccess = {
                Log.d("CertViewModel_CERTIFY", "✅ 인증자: 요청 성공")
                onSuccess()
            },
            onError = { error ->
                _errorMessage.postValue("요청 실패: ${error.message}")
                Log.e("CertViewModel_CERTIFY", "❌ 인증자: 요청 실패", error)
            }
        )
    }

    /**
     * ✅ [복원] 테스트용: 구독 후 바로 인증 요청을 보내 서버의 응답을 확인하는 함수
     */
    fun test_subscribeAndSendRequest(sessionId: Long, adminId: Long) {
        Log.d("CertViewModel_TEST", "🚀 테스트 시작 (Session: $sessionId)")
        _connectionStatus.value = ConnectionStatus.CONNECTING
        _sessionId.value = sessionId
        stompClient?.disconnect() // 이전 연결 정리

        stompClient = CertificationWebSocketClient(
            wsUrl = wsUrl,
            tokenProvider = tokenProvider
        )

        stompClient?.connectAndSubscribe(
            sessionId = sessionId, // sessionId 전달
            onConnected = {
                _connectionStatus.postValue(ConnectionStatus.CONNECTED)
                Log.d("CertViewModel_TEST", "✅ 테스트: 연결 및 구독 성공. 이제 인증 요청을 보냅니다...")

                // 연결 성공 콜백 안에서 '인증 요청'을 바로 보냄
                stompClient?.sendCertificationRequest(adminId, sessionId)
            },
            onCertificationMessage = { jsonBody ->
                // 이 콜백으로 응답이 오는지 확인하는 것이 테스트의 핵심
                Log.d("CertViewModel_TEST", "📩 테스트: 서버로부터 메시지 수신 성공! -> $jsonBody")
                handleProgressUpdate(jsonBody)
            },
            onError = { error ->
                _connectionStatus.postValue(ConnectionStatus.FAILED)
                _errorMessage.postValue("테스트 중 에러 발생: ${error.message}")
                Log.e("CertViewModel_TEST", "❌ 테스트: 에러 발생", error)
            }
        )
    }


    private fun handleProgressUpdate(jsonBody: String) {
        // 메시지 파싱 로직 (기존과 동일)
        try {
            val progress = gson.fromJson(jsonBody, CertificationProgressDto::class.java)
            _currentCount.postValue(progress.count)
            when (progress.type) {
                "progress" -> { /* 진행중 상태 처리 */ }
                "completed" -> {
                    _isCompleted.postValue(true)
                    _completionMessage.postValue(progress.message ?: "인증 완료")
                    _userIds.postValue(progress.userIds ?: emptyList())
                }
            }
        } catch (e: Exception) {
            Log.e("JSON_PARSE", "메시지 파싱 실패", e)
        }
    }

    // ... saveGroupUsage, disconnect, onCleared 등 나머지 함수는 동일하게 유지 ...
    fun saveGroupUsage(request: SaveUsageRequestDto) {
        viewModelScope.launch {
            when (saveUseCase(request)) {
                is RetrofitResult.Success -> Log.d("CertifyViewModel", "그룹 사용 내역 저장 성공")
                is RetrofitResult.Error -> { /* 에러 처리 */ }
                is RetrofitResult.Fail -> { /* 실패 처리 */ }
            }
        }
    }

    fun disconnect() {
        stompClient?.disconnect()
        Log.d("CertifyViewModel", "🔌 연결 해제 요청")
    }

    override fun onCleared() {
        super.onCleared()
        stompClient?.disconnect()
    }
}