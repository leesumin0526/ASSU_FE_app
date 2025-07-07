package com.example.assu_fe_app.data.dto.chatting

data class ChattingListItem(
    val roomId: String,
    val lastMessage: String,
    val lastChatTime: String,
    val unreadCount: Int,
    val profileImage: Int,
    val opponentName: String,
)
