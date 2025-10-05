package com.assu.app.data.dto.chatting.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BlockRequestDto(
    val opponentId: Long
)
