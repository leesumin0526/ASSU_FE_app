package com.assu.app.domain.model.chatting

data class LeaveChattingRoomModel(
    val roomId: Long,
    val isLeftSuccessfully: Boolean,
    val isRoomDeleted: Boolean
)
