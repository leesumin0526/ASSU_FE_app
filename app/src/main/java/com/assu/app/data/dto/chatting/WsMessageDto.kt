package com.assu.app.data.dto.chatting

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WsMessageDto(
    val messageId: Long,
    val roomId: Long,
    val senderId: Long,
    val receiverId: Long,
    val message: String,
    val sentAt: String,   // "2025-09-08 21:33:00" 형태
    val unreadCountForSender: Int ?= 0
)

fun WsMessageDto.toUiItem(myId: Long): ChattingMessageItem {
    return if (senderId == myId) {
        ChattingMessageItem.MyMessage(
            messageId = messageId,
            message = message,
            sentAt = sentAt,
            isRead = (unreadCountForSender == 0),
            unreadCountForSender = unreadCountForSender ?: 0
        )
    } else {
        ChattingMessageItem.OtherMessage(
            messageId = messageId,
            profileImageUrl = "",
            message = message,
            sentAt = sentAt,
            isRead = true
        )
    }
}