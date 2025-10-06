package com.ssu.assu.data.dto.chatting

data class ChattingRoomListItem(
    val roomId: String,
    val lastMessage: String,
    val lastChatTime: String,
    val unreadCount: Int,
    val profileImage: Int,
    val opponentName: String,
)
