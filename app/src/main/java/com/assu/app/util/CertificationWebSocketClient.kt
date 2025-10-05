package com.assu.app.util

import android.util.Log
import com.assu.app.data.dto.certification.request.GroupSessionRequest
import com.assu.app.data.local.AccessTokenProvider
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.reactivex.disposables.CompositeDisposable
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompHeader

class CertificationWebSocketClient(
    private val wsUrl: String,
    private val tokenProvider: AccessTokenProvider
) {
    private lateinit var stompClient: StompClient
    private val disposables = CompositeDisposable()

    @Volatile private var isConnected = false
    private var topicDisposable: io.reactivex.disposables.Disposable? = null

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()
    private val groupSessionRequestAdapter = moshi.adapter(GroupSessionRequest::class.java)

    fun connectAndSubscribe(
        sessionId: Long,
        onConnected: () -> Unit,
        onCertificationMessage: (String) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val authHeader = tokenProvider.bearer()
        Log.d("CertWS", "Auth header before connect: $authHeader")

        val handshakeHeaders: Map<String, String> =
            if (!authHeader.isNullOrBlank()) mapOf("Authorization" to authHeader)
            else emptyMap()

        val connectHeaders = mutableListOf<StompHeader>()
        authHeader?.let { connectHeaders.add(StompHeader("Authorization", it)) }

        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, wsUrl, handshakeHeaders)
        stompClient.withClientHeartbeat(10_000).withServerHeartbeat(10_000)

        disposables.add(
            stompClient.lifecycle().subscribe { event ->
                when (event.type) {
                    LifecycleEvent.Type.OPENED -> {
                        // WebSocket이 열리고 STOMP CONNECT 프레임이 전송된 후
                        // 곧바로 CONNECTED 응답을 받게 됨
                        Log.d("CertWS", "🔌 WebSocket OPENED - STOMP CONNECT 프레임 전송됨")

                        // 짧은 지연 후 연결 상태를 true로 설정
                        // 실제로는 CONNECTED 프레임을 받아야 하지만,
                        // 이 라이브러리에서는 OPENED 이후 즉시 사용 가능
                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            isConnected = true
                            retryCount = 0

                            // 토픽 구독
                            topicDisposable?.dispose()
                            topicDisposable = stompClient.topic("/certification/progress/$sessionId").subscribe { message ->
                                Log.d("CertWS", "📩 토픽 메시지 수신: ${message.payload}")
                                onCertificationMessage(message.payload)
                            }

                            Log.d("CertWS", "✅ STOMP 연결 완료 및 토픽 구독 완료")
                            onConnected()
                        }, 100) // 100ms 지연
                    }
                    LifecycleEvent.Type.CLOSED -> {
                        isConnected = false
                        topicDisposable?.dispose()
                        Log.d("CertWS", "🔌 WebSocket CLOSED")
//                        scheduleReconnect(sessionId, onConnected, onCertificationMessage, onError)
                    }
                    LifecycleEvent.Type.ERROR -> {
                        isConnected = false
                        topicDisposable?.dispose()
                        Log.e("CertWS", "🔌 WebSocket ERROR", event.exception)
                        onError(event.exception ?: Exception("WebSocket connection error"))
//                        scheduleReconnect(sessionId, onConnected, onCertificationMessage, onError)
                    }
                    else -> Unit
                }
            }
        )

        stompClient.connect(connectHeaders)
    }

    fun connectAndSend(
        adminId: Long,
        sessionId: Long,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val authHeader = tokenProvider.bearer()
        val handshakeHeaders: Map<String, String> =
            if (!authHeader.isNullOrBlank()) mapOf("Authorization" to authHeader)
            else emptyMap()

        val singleSendStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, wsUrl, handshakeHeaders)
        val connectHeaders = mutableListOf<StompHeader>()
        authHeader?.let { connectHeaders.add(StompHeader("Authorization", it)) }

        val tempDisposables = CompositeDisposable()

        tempDisposables.add(
            singleSendStompClient.lifecycle().subscribe { event ->
                when (event.type) {
                    LifecycleEvent.Type.OPENED -> {
                        Log.d("CertWS", "✅ 단일 전송용 WebSocket OPENED")

                        // 짧은 지연 후 메시지 전송
                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            val payload = GroupSessionRequest(adminId = adminId, sessionId = sessionId)
                            val jsonPayload = groupSessionRequestAdapter.toJson(payload)

                            tempDisposables.add(
                                singleSendStompClient.send("/app/certify", jsonPayload)
                                    .subscribe(
                                        {
                                            Log.d("CertWS", "✅ 메시지 전송 성공")
                                            onSuccess()
                                            singleSendStompClient.disconnect()
                                            tempDisposables.dispose()
                                        },
                                        { error ->
                                            Log.e("CertWS", "❌ 메시지 전송 실패", error)
                                            onError(error)
                                            singleSendStompClient.disconnect()
                                            tempDisposables.dispose()
                                        }
                                    )
                            )
                        }, 100) // 100ms 지연
                    }
                    LifecycleEvent.Type.ERROR -> {
                        onError(event.exception ?: Exception("WebSocket connection error for send"))
                        tempDisposables.dispose()
                    }
                    else -> Unit
                }
            }
        )

        singleSendStompClient.connect(connectHeaders)
    }

    fun disconnect() {
        reconnectHandler?.removeCallbacksAndMessages(null)
        topicDisposable?.dispose()
        if (this::stompClient.isInitialized) {
            stompClient.disconnect()
        }
        disposables.clear()
        isConnected = false
        retryCount = 0
    }

    // 재연결 로직
    private var retryCount = 0
    private var reconnectHandler: android.os.Handler? = null

    private fun scheduleReconnect(
        sessionId: Long,
        onConnected: () -> Unit,
        onCertificationMessage: (String) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        if (reconnectHandler == null) {
            reconnectHandler = android.os.Handler(android.os.Looper.getMainLooper())
        }
        val delayMs = (3000L * (1 shl retryCount)).coerceAtMost(15_000L)

        reconnectHandler?.postDelayed({
            connectAndSubscribe(sessionId, onConnected, onCertificationMessage, onError)
        }, delayMs)

        if (retryCount < 4) retryCount++
    }

    // 기존 연결에서 메시지 전송 (수정됨)
    fun sendCertificationRequest(adminId: Long, sessionId: Long) {
        Log.d("CertWS", "🚀 sendCertificationRequest 호출 - isConnected: $isConnected, stompClient 초기화: ${this::stompClient.isInitialized}")

        if (!this::stompClient.isInitialized) {
            Log.w("CertWS", "❌ StompClient가 초기화되지 않음")
            return
        }

        if (!stompClient.isConnected) {
            Log.w("CertWS", "❌ STOMP 클라이언트가 연결되지 않음")
            return
        }

        if (!isConnected) {
            Log.w("CertWS", "❌ 내부 연결 플래그가 false")
            return
        }

        val payload = GroupSessionRequest(adminId = adminId, sessionId = sessionId)
        val jsonPayload = groupSessionRequestAdapter.toJson(payload)

        Log.d("CertWS", "📤 메시지 전송 시도: $jsonPayload")

        disposables.add(
            stompClient.send("/app/certify", jsonPayload).subscribe(
                {
                    Log.i("CertWS", "✅ 기존 연결로 메시지 전송 성공")
                },
                { error ->
                    Log.e("CertWS", "❌ 기존 연결로 메시지 전송 실패", error)
                }
            )
        )
    }
}