package com.ssu.assu.data.dto.chatting.response

import com.ssu.assu.domain.model.chatting.UnblockOpponentModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UnblockResponseDto(
    val memberId: Long,
    val name: String
) {
    fun toModel() = UnblockOpponentModel(
        memberId = this.memberId,
        name = this.name
    )
}
