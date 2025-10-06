package com.ssu.assu.domain.model.chatting

data class CheckBlockModel(
    val memberId: Long,
    val name: String,
    val blocked: Boolean
)
