package com.ssu.assu.domain.model.chatting

data class GetChattingRoomListModel(
    val roomId: Long,
    val lastMessage: String,
    val lastMessageTime: String,
    val unreadMessagesCount: Long,
    val opponentId: Long,
    val opponentName: String,
    val opponentProfileImage: String,
    val phoneNumber: String
)
