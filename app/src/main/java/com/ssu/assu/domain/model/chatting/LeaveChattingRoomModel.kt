package com.ssu.assu.domain.model.chatting

data class LeaveChattingRoomModel(
    val roomId: Long,
    val isLeftSuccessfully: Boolean,
    val isRoomDeleted: Boolean
)
