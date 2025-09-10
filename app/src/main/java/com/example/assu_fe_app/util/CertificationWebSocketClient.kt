package com.example.assu_fe_app.util

import com.example.assu_fe_app.data.dto.certification.CertificationRequestDto
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class CertificationWebSocketClient(
    private val serverUrl: String,
    private val authToken: String,
    private val listener: StompListener
) {
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient()
    private var isConnected = false
    private var sessionId: String? = null

    interface StompListener {
        fun onConnected()
        fun onMessage(destination: String, body: String)
        fun onError(error: String)
        fun onDisconnected()
    }

    fun connect() {
        val request = Request.Builder()
            .url(serverUrl)
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                // STOMP CONNECT 프레임 전송
                val connectFrame = buildString {
                    appendLine("CONNECT")
                    appendLine("accept-version:1.1,1.0")
                    appendLine("heart-beat:10000,10000")
                    appendLine("Authorization: Bearer $authToken")
                    appendLine()
                    append("\u0000") // null terminator
                }
                webSocket.send(connectFrame)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                handleStompMessage(text)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                listener.onError("Connection failed: ${t.message}")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                isConnected = false
                listener.onDisconnected()
            }
        })
    }

    private fun handleStompMessage(message: String) {
        when {
            message.startsWith("CONNECTED") -> {
                isConnected = true
                listener.onConnected()
            }
            message.startsWith("MESSAGE") -> {
                val lines = message.split("\n")
                var destination = ""
                var body = ""
                var isBody = false

                for (line in lines) {
                    when {
                        line.startsWith("destination:") -> {
                            destination = line.substring(12)
                        }
                        line.isEmpty() -> {
                            isBody = true
                        }
                        isBody -> {
                            body += line
                        }
                    }
                }
                listener.onMessage(destination, body.replace("\u0000", ""))
            }
            message.startsWith("ERROR") -> {
                listener.onError("STOMP Error: $message")
            }
        }
    }

    fun subscribe(destination: String) {
        if (!isConnected) return

        val subscribeFrame = buildString {
            appendLine("SUBSCRIBE")
            appendLine("id:sub-0")
            appendLine("destination:$destination")
            appendLine()
            append("\u0000")
        }
        webSocket?.send(subscribeFrame)
    }

    fun send(destination: String, body: String) {
        if (!isConnected) return

        val sendFrame = buildString {
            appendLine("SEND")
            appendLine("destination:$destination")
            appendLine("content-type:application/json")
            appendLine("content-length:${body.length}")
            appendLine()
            append(body)
            append("\u0000")
        }
        webSocket?.send(sendFrame)
    }

    fun disconnect() {
        if (isConnected) {
            val disconnectFrame = "DISCONNECT\n\n\u0000"
            webSocket?.send(disconnectFrame)
        }
        webSocket?.close(1000, "Normal closure")
        client.dispatcher.executorService.shutdown()
    }

}