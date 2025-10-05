package com.assu.app.data.socket

import android.util.Log
import com.assu.app.data.local.AccessTokenProvider
import com.squareup.moshi.Json
import io.reactivex.disposables.CompositeDisposable
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompHeader

class ChatSocketClient(
    private val wsUrl: String,
    private val accessTokenProvider: AccessTokenProvider
) {
    private lateinit var stomp: StompClient
    private val disposables = CompositeDisposable()

    @Volatile private var connected = false
    private val pendingQueue = ArrayDeque<String>() // 연결 전 보낼 메시지 큐

    private var roomTopicDisposable: io.reactivex.disposables.Disposable? = null
    private var userQueueDisposable: io.reactivex.disposables.Disposable? = null

    private val moshi = com.squareup.moshi.Moshi.Builder()
        .addLast(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
        .build()
    private val sendAdapter = moshi.adapter(SendPayload::class.java)

    private data class SendPayload(
        val roomId: Long,
        val senderId: Long,
        val receiverId: Long,
        val message: String,
        @Json(name = "send_time")val sendTime: String,
        @Json(name = "type")val type: String
    )


    fun connect(
        roomId: Long,
        onConnected: () -> Unit,
        onMessageJson: (String) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val authHeader = accessTokenProvider.bearer()
        // 1) 핸드셰이크(HTTP 업그레이드) 헤더: Map<String, String>
        val handshakeHeaders: Map<String, String> =
            if (!authHeader.isNullOrBlank()) mapOf("Authorization" to authHeader!!)
            else emptyMap()

        // 2) StompClient 생성 (세 번째 인자는 Map)
        stomp = Stomp.over(
            Stomp.ConnectionProvider.OKHTTP,
            wsUrl,
            handshakeHeaders)

        stomp.withClientHeartbeat(10_000)
        stomp.withServerHeartbeat(10_000)

        // 3) STOMP CONNECT 프레임 헤더: List<StompHeader>
        val headers = mutableListOf<StompHeader>()
        authHeader?.let { headers += StompHeader("Authorization", it) }

        // lifecycle 이벤트 구독
        disposables.add(
            stomp.lifecycle().subscribe { ev ->
                when (ev.type) {
                    LifecycleEvent.Type.OPENED -> {
                        connected = true
                        retry = 0
                        // ★ 이전 구독 정리 후 새로 구독
                        roomTopicDisposable?.dispose()
                        roomTopicDisposable = stomp.topic("/sub/chat/$roomId").subscribe { msg ->
                            Log.d("WS", "받은 메시지: ${msg.payload}")   // 꼭 확인
                            onMessageJson(msg.payload)
                        }
                        flushQueue()
                        onConnected()
                    }
                    LifecycleEvent.Type.CLOSED -> {
                        connected = false
                        roomTopicDisposable?.dispose()
                        roomTopicDisposable = null
                        scheduleReconnect(roomId, onConnected, onMessageJson, onError)
                    }
                    LifecycleEvent.Type.ERROR -> {
                        connected = false
                        roomTopicDisposable?.dispose()
                        roomTopicDisposable = null
                        onError(ev.exception ?: Exception("WS error"))
                        scheduleReconnect(roomId, onConnected, onMessageJson, onError)
                    }
                    else -> Unit
                }
            }
        )
        stomp.connect(headers)
    }

    fun subscribeToUserQueue(onUpdateReceived: (String) -> Unit) {
        if (!connected || !this::stomp.isInitialized || !stomp.isConnected) {
            Log.w("WS", "Stomp is not connected. Cannot subscribe to user queue.")
            // TODO: 연결 후 구독을 재시도하는 로직을 추가할 수도 있습니다.
            return
        }

        // 기존 구독이 있다면 해제
        userQueueDisposable?.dispose()

        userQueueDisposable = stomp.topic("/user/queue/updates").subscribe(
            { msg ->
                Log.d("WS_USER", "개인 알림 수신: ${msg.payload}")
                onUpdateReceived(msg.payload)
            },
            { throwable ->
                Log.e("WS_USER", "개인 알림 구독 오류", throwable)
            }
        )
        disposables.add(userQueueDisposable!!)
    }

    // ▼▼▼▼▼ [추가 2] 개인 큐 구독 해제 함수 ▼▼▼▼▼
    fun unsubscribeFromUserUpdates() {
        userQueueDisposable?.dispose()
        userQueueDisposable = null
    }


    // 기존 sendMessage 교체
    fun sendMessage(
        roomId: Long,
        senderId: Long,
        receiverId: Long,
        message: String,
        type: String = "TEXT") {

        val formatted = java.text.SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            java.util.Locale.getDefault()
        ).format(java.util.Date())

        val json = sendAdapter.toJson(
            SendPayload(
                roomId, senderId, receiverId, message, formatted, type))

        if (!connected || !this::stomp.isInitialized || !stomp.isConnected) {
            pendingQueue.addLast(json)
            return
        }
        disposables.add(
            stomp.send("/pub/send", json).subscribe(
                { /* ok */ },
                { e -> e.printStackTrace() }
            )
        )
    }

    private fun flushQueue() {
        while (pendingQueue.isNotEmpty() && connected && stomp.isConnected) {
            val payload = pendingQueue.removeFirst()
            disposables.add(stomp.send("/pub/send", payload).subscribe(
                { /* ok */ },
                { e -> android.util.Log.e("WS", "flush send error", e) }
            ))
        }
    }

    // 중단
    private var reconnectHandler: android.os.Handler? = null
    fun disconnect() {
        roomTopicDisposable?.dispose()
        userQueueDisposable?.dispose()
        roomTopicDisposable = null
        userQueueDisposable = null
        if (this::stomp.isInitialized) {
            stomp.disconnect()
        }
        connected = false
        disposables.clear()
        retry = 0
        reconnectHandler?.removeCallbacksAndMessages(null)
    }

    // 재연결
    private var retry = 0
    private fun scheduleReconnect(
        roomId: Long,
        onConnected: () -> Unit,
        onMessageJson: (String) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val delayMs = (1000L * (1 shl retry)).coerceAtMost(15_000L)
        if (reconnectHandler == null) {
            reconnectHandler = android.os.Handler(android.os.Looper.getMainLooper())
        }
        reconnectHandler?.postDelayed({
            connect(roomId, onConnected, onMessageJson, onError)
        }, delayMs)
        if (retry < 4) retry++
    }

}