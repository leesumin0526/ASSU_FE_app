package com.assu.app.data.dto.chatting.response

import com.assu.app.domain.model.chatting.ReadChattingModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReadChattingResponseDto(
    val roomId: Long,
    val readerId: Long,
    val readMessagesId: List<Long>,
    val readCount: Int,
    val isRead: Boolean,
) {
    fun toModel() = ReadChattingModel(
        roomId = this.roomId,
        readerId = this.readerId,
        readMessagesId = this.readMessagesId,
        readCount = this.readCount,
        isRead = this.isRead
    )
}
