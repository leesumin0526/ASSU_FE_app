package com.example.assu_fe_app.data.dto.chatting.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateChatRoomRequestDto(
    val adminId: Long?,
    val partnerId: Long?
)
