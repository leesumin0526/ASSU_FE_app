package com.example.assu_fe_app.domain.model.chatting

data class CreateChatRoomModel(
    val roomId: Long,
    val adminViewName: String,
    val partnerViewName: String
)
