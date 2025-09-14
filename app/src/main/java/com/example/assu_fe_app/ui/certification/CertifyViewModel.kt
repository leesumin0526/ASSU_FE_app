package com.example.assu_fe_app.ui.certification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.assu_fe_app.util.CertificationWebSocketClient
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.assu_fe_app.BuildConfig
import com.example.assu_fe_app.data.dto.certification.response.CertificationProgressDto
import com.example.assu_fe_app.data.dto.certification.request.GroupSessionRequest
import com.example.assu_fe_app.data.dto.usage.SaveUsageRequestDto
import com.example.assu_fe_app.domain.usecase.usage.SaveUsageUseCase
import com.example.assu_fe_app.util.RetrofitResult
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CertifyViewModel @Inject constructor(
    private val saveUseCase : SaveUsageUseCase
) : ViewModel() {
    private val _connectionStatus = MutableLiveData<ConnectionStatus>()
    val connectionStatus: LiveData<ConnectionStatus> = _connectionStatus

    private val _currentCount = MutableLiveData<Int>()
    val currentCount: LiveData<Int> = _currentCount

    private val _targetCount = MutableLiveData<Int>()
    val targetCount: LiveData<Int> = _targetCount

    private val _isCompleted = MutableLiveData<Boolean>()
    val isCompleted: LiveData<Boolean> = _isCompleted

    private val _userIds = MutableLiveData<List<Long>>()
    val userIds : LiveData<List<Long>> = _userIds
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

    private fun handleProgressUpdate(jsonBody: String) {
        try {
            val progress = gson.fromJson(jsonBody, CertificationProgressDto::class.java)

            // 현재 카운트는 항상 업데이트
            _currentCount.postValue(progress.count)

            when (progress.type) {
                "progress" -> {
                    Log.d("JSON_PARSE🍭", "Progress update received: $jsonBody")
                }
                "completed" -> {
                    Log.d("JSON_PARSE🍭", "Completed update received: $jsonBody")
                    // 완료 상태 처리
                    _isCompleted.value = true
                    _completionMessage.value = progress.message ?: "메세지가 비어있습니다. "
                    _userIds.value = progress.userIds ?: emptyList()
                    Log.d("userIds 값 update", _userIds.value.toString())
                }
            }
        } catch (e: Exception) {
            Log.e("JSON_PARSE", "Failed to parse progress update", e)
        }
    }

    fun saveGroupUsage(
        request : SaveUsageRequestDto
    ){
        viewModelScope.launch {
            when ( val result = saveUseCase(request) ){
                is RetrofitResult.Success -> {
                    Log.d("데이터 저장 성공", "그룹 제휴 사용 데이터를 성공적으로 저장하였습니다.")
                }

                is RetrofitResult.Error -> {

                }
                is RetrofitResult.Fail ->{}
            }
        }
    }

    fun subscribeToProgress(sessionId: Long, authToken: String) {
        if (authToken.isEmpty()) {
            _errorMessage.value = "인증 토큰이 없습니다."
            return
        }

        _connectionStatus.value = ConnectionStatus.CONNECTING
        _sessionId.value = sessionId

        stompClient = CertificationWebSocketClient(
            serverUrl = BuildConfig.CERTIFICATION_URL,
            authToken = authToken,
            listener = object : CertificationWebSocketClient.StompListener {
                override fun onConnected() {
                    _connectionStatus.postValue(ConnectionStatus.CONNECTED)
                    // TODO 아래 2줄 주석 필요 : 세션별 진행 상황 구독만 함 (인증 요청은 하지 않음)
                    stompClient?.subscribe("/certification/progress/$sessionId")
                    Log.d("CertifyViewModel", "대표자가 진행 상황을 구독합니다. ")
                }

                override fun onMessage(destination: String, body: String) {
                    handleProgressUpdate(body)
                }

                override fun onError(error: String) {
                    _connectionStatus.postValue(ConnectionStatus.FAILED)
                    _errorMessage.postValue("연결 실패: $error")
                    Log.e("WebSocket", error)
                }

                override fun onDisconnected() {
                    _connectionStatus.postValue(ConnectionStatus.DISCONNECTED)
                }
            }
        )
        stompClient?.connect()
    }

    fun connectAndCertify(sessionId: Long, adminId: Long, authToken: String) {

        Log.d("CertifyViewModel", "connectAndCertify -> ")
        if (authToken.isEmpty()) {
            _errorMessage.value = "인증 토큰이 없습니다."
            return
        }

        _connectionStatus.value = ConnectionStatus.CONNECTING
        _sessionId.value = sessionId
        Log.d("CertifyViewModel", "sessionId: $sessionId")

        // 이전 연결이 있다면 정리
        stompClient?.disconnect()

        stompClient = CertificationWebSocketClient(
            serverUrl = BuildConfig.CERTIFICATION_URL,
            authToken = authToken,
            listener = object : CertificationWebSocketClient.StompListener {
                override fun onConnected() {
                    _connectionStatus.postValue(ConnectionStatus.CONNECTED)

                    // 임시 테스트 용
                    stompClient?.subscribe("/certification/progress/$sessionId")
                    Log.d("CertifyViewModel", "대표자가 진행 상황을 구독합니다. ")
                    // 인증 요청만 전송 (구독은 하지 않음)
                    val request = GroupSessionRequest(
                        adminId = adminId,
                        sessionId = sessionId
                    )

                    stompClient?.send(
                        destination = "/app/certify",
                        body = gson.toJson(request)
                    )

                    Log.d("CertifyViewModel", "인증자가 인증 요청을 보냈습니다. ")
                }

                override fun onMessage(destination: String, body: String) {
                    handleProgressUpdate(body)
                }

                override fun onError(error: String) {
                    _connectionStatus.postValue(ConnectionStatus.FAILED)
                    _errorMessage.postValue("연결 실패: $error")
                    Log.e("WebSocket", error)
                }

                override fun onDisconnected() {
                    _connectionStatus.postValue(ConnectionStatus.DISCONNECTED)
                }
            }
        )
        stompClient?.connect()
    }

    // 연결 해제 함수
    fun disconnect() {
        stompClient?.disconnect()
        _connectionStatus.value = ConnectionStatus.DISCONNECTED
    }

    // 재연결 함수
    fun reconnect() {
        val currentSessionId = _sessionId.value
        if (currentSessionId != null) {
            val authToken = getStoredAuthToken() // 토큰을 다시 가져와야 함
            if (authToken.isNotEmpty()) {
                disconnect()
                subscribeToProgress(currentSessionId, authToken)
            }
        }
    }

    // 현재 연결 상태 확인
    fun isConnected(): Boolean {
        return _connectionStatus.value == ConnectionStatus.CONNECTED
    }

    // 에러 메시지 초기화
    fun clearErrorMessage() {
        _errorMessage.value = ""
    }

    private fun getStoredAuthToken(): String {
        // Context가 필요하므로 실제로는 Repository나 DataStore를 통해 가져와야 함
        // 여기서는 예시로만 작성
        return ""
    }

    override fun onCleared() {
        super.onCleared()
        stompClient?.disconnect()
    }
}