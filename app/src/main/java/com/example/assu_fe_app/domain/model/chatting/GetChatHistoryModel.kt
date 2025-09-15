package com.example.assu_fe_app.domain.model.chatting

data class GetChatHistoryModel(
    val roomId: Long,
    val messages: List<ChatMessageModel>
)

data class ChatMessageModel(
    val messageId: Long,
    val message: String?,
    val sendTime: String,
    val isRead: Boolean,
    val isMyMessage: Boolean,
    val profileImageUrl: String
)
