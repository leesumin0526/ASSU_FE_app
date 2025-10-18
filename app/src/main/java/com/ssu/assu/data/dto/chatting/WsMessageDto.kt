package com.ssu.assu.data.dto.chatting

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WsMessageDto(
    val messageId: Long,
    val roomId: Long,
    val senderId: Long,
    val receiverId: Long,
    val message: String,
    val sentAt: String,   // "2025-09-08 21:33:00" 형태
    val unreadCountForSender: Int ?= 0,
    val messageType: String ?= "TEXT",
)

fun WsMessageDto.toUiItem(myId: Long): ChattingMessageItem {
    return if (messageType == "GUIDE") {
        ChattingMessageItem.GuideMessageItem(
            messageId = messageId,
            guideMessage = message,
            sentAt = sentAt
        )
    } else {
        // 기존의 MyMessage / OtherMessage 구분 로직
        if (senderId == myId) {
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
                profileImageUrl = "", // 실시간 메시지에는 프로필 정보가 없으므로 빈 값 처리
                message = message,
                sentAt = sentAt,
                isRead = true
            )
        }
    }
}