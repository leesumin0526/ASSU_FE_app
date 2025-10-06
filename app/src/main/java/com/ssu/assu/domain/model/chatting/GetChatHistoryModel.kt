package com.ssu.assu.domain.model.chatting

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
    val profileImageUrl: String,
    val unreadCountForSender: Int ? =0
)
