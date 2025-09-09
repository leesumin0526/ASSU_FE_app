package com.example.assu_fe_app.domain.model.chatting

data class GetChattingRoomListModel(
    val roomId: Long,
    val lastMessage: String,
    val lastMessageTime: String,
    val unreadMessageCount: Long,
    val opponentId: Long,
    val opponentName: String,
    val opponentProfileImage: String
)
