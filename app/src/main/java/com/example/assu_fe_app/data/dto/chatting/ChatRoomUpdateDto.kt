package com.example.assu_fe_app.data.dto.chatting

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatRoomUpdateDTO(
    @Json(name = "roomId")
    val roomId: Long,

    @Json(name = "lastMessage")
    val lastMessage: String,

    @Json(name = "lastMessageTime")
    val lastMessageTime: String, // 서버의 LocalDateTime을 String으로 받는 것이 가장 안정적입니다.

    @Json(name = "unreadCount")
    val unreadCount: Long
)