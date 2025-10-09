package com.ssu.assu.data.dto.chatting.response

import com.ssu.assu.domain.model.chatting.BlockOpponentModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BlockResponseDto(
    val memberId: Long,
    val name: String
) {
    fun toModel() = BlockOpponentModel(
        memberId = this.memberId,
        name = this.name
    )
}
