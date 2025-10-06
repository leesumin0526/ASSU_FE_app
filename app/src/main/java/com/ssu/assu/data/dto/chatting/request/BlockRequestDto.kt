package com.ssu.assu.data.dto.chatting.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BlockRequestDto(
    val opponentId: Long
)
