package com.example.assu_fe_app.data.dto.chatting.response

import com.example.assu_fe_app.domain.model.chatting.CreateChatRoomModel

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
