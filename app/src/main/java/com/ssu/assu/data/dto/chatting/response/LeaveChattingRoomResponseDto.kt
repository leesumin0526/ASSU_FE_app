package com.ssu.assu.data.dto.chatting.response

import com.ssu.assu.domain.model.chatting.LeaveChattingRoomModel
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
