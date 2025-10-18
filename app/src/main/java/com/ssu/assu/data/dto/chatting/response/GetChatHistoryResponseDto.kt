package com.ssu.assu.data.dto.chatting.response

import com.ssu.assu.domain.model.chatting.ChatMessageModel
import com.ssu.assu.domain.model.chatting.GetChatHistoryModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetChatHistoryResponseDto(
    val roomId: Long,
    val messages: List<ChatMessageResponseDto>
) {
    fun toModel() = GetChatHistoryModel(
        roomId = this.roomId,
        messages = this.messages.map { it.toModel() }
    )
}

@JsonClass(generateAdapter = true)
data class ChatMessageResponseDto(
    val messageId: Long,
    val message: String?,        // 이모지 포함 가능 → String Nullable 안전
    val sendTime: String,        // "2025-08-22T14:30:00" 같은 ISO 문자열 그대로
    val isRead: Boolean ?= false,
    val isMyMessage: Boolean ?= false ,
    val profileImageUrl: String ?= null,
    val unreadCountForSender: Int ?= 0,
    val messageType: String ?= "TEXT"
) {
    fun toModel() = ChatMessageModel(
        messageId = this.messageId,
        message = this.message,
        sendTime = this.sendTime,
        isRead = this.isRead ?: false,
        isMyMessage = this.isMyMessage ?: false,
        profileImageUrl = this.profileImageUrl ?: "",
        unreadCountForSender = this.unreadCountForSender ?: 0,
        messageType = this.messageType ?: "TEXT"
    )
}