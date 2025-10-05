package com.assu.app.domain.model.chatting

data class GetBlockListModel(
    val memberId: Long,
    val name: String,
    val blockDate: String,
)
