package com.ssu.assu.data.dto.chatting.response

import com.squareup.moshi.Json
import com.ssu.assu.domain.model.chatting.CreateChatRoomModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateChatRoomResponseDto(
    val roomId: Long,
    val adminViewName: String,
    val partnerViewName: String,
    @Json(name = "isNew")
    val isNew: Boolean = false
) {
    fun toModel() = CreateChatRoomModel(
        roomId = this.roomId,
        adminViewName = this.adminViewName,
        partnerViewName = this.partnerViewName,
        isNew = this.isNew
    )
}
