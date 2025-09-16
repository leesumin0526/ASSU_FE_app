package com.example.assu_fe_app.data.socket

import com.example.assu_fe_app.data.local.TokenProvider
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.reactivex.disposables.CompositeDisposable
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompHeader

class ChatSocketClient(
    private val wsUrl: String,
    private val tokenProvider: TokenProvider
) {
    private lateinit var stomp: StompClient
    private val disposables = CompositeDisposable()

    @Volatile private var connected = false
    private val pendingQueue = ArrayDeque<String>() // 연결 전 보낼 메시지 큐
    private var topicDisposable: io.reactivex.disposables.Disposable? = null

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
        val authHeader = tokenProvider.bearer()
        android.util.Log.d("WS", "🔑 Authorization header being sent = $authHeader")
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
                        topicDisposable?.dispose()
                        topicDisposable = stomp.topic("/sub/chat/$roomId").subscribe { msg ->
                            onMessageJson(msg.payload)
                        }
                        flushQueue()
                        onConnected()
                    }
                    LifecycleEvent.Type.CLOSED -> {
                        connected = false
                        topicDisposable?.dispose()
                        topicDisposable = null
                        scheduleReconnect(roomId, onConnected, onMessageJson, onError)
                    }
                    LifecycleEvent.Type.ERROR -> {
                        connected = false
                        topicDisposable?.dispose()
                        topicDisposable = null
                        onError(ev.exception ?: Exception("WS error"))
                        scheduleReconnect(roomId, onConnected, onMessageJson, onError)
                    }
                    else -> Unit
                }
            }
        )
        stomp.connect(headers)
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
        topicDisposable?.dispose()
        topicDisposable = null
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