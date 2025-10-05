package com.assu.app.domain.model.chatting

data class CreateChatRoomModel(
    val roomId: Long,
    val adminViewName: String,
    val partnerViewName: String
)
