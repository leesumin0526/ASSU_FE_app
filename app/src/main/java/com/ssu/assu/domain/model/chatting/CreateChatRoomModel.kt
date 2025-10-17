package com.ssu.assu.domain.model.chatting

data class CreateChatRoomModel(
    val roomId: Long,
    val adminViewName: String,
    val partnerViewName: String,
    val isNew: Boolean
)
