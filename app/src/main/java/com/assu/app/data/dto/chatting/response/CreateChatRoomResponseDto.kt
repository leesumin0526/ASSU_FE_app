package com.assu.app.data.dto.chatting.response

import com.assu.app.domain.model.chatting.CreateChatRoomModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateChatRoomResponseDto(
    val roomId: Long,
    val adminViewName: String,
    val partnerViewName: String
) {
    fun toModel() = CreateChatRoomModel(
        roomId = this.roomId,
        adminViewName = this.adminViewName,
        partnerViewName = this.partnerViewName
    )
}
