package com.example.assu_fe_app.data.dto.chatting.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateChatRoomRequestDto(
    val storeId: Long,
    val partnerId: Long
)
