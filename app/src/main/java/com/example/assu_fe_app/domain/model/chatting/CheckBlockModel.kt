package com.example.assu_fe_app.domain.model.chatting

data class CheckBlockModel(
    val memberId: Long,
    val name: String,
    val blocked: Boolean
)
