package com.assu.app.data.dto.chatting.response

import com.assu.app.domain.model.chatting.LeaveChattingRoomModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LeaveChattingRoomResponseDto(
    val roomId: Long,
    val isLeftSuccessfully: Boolean,
    val isRoomDeleted: Boolean
) {
    fun toModel() = LeaveChattingRoomModel(
        roomId = this.roomId,
        isLeftSuccessfully = this.isLeftSuccessfully,
        isRoomDeleted = this.isRoomDeleted
    )
}
