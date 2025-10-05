package com.assu.app.data.dto.chatting.response

import com.assu.app.domain.model.chatting.UnblockOpponentModel
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
