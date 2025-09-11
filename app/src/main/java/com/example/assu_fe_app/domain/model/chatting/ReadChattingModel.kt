package com.example.assu_fe_app.domain.model.chatting

data class ReadChattingModel(
    val roomId: Long,
    val readerId: Long,
    val readMessagesId: List<Long>,
    val readCount: Int,
    val isRead: Boolean,
)
