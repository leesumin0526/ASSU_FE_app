package com.ssu.assu.domain.model.chatting

data class ReadChattingModel(
    val roomId: Long,
    val readerId: Long,
    val readMessagesId: List<Long>,
    val readCount: Int,
    val isRead: Boolean,
)
