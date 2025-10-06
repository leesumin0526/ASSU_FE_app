package com.ssu.assu.data.dto.chatting.response

import com.ssu.assu.domain.model.chatting.CheckBlockModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CheckBlockResponseDto(
    val memberId: Long,
    val name: String,
    val blocked: Boolean
) {
    fun toModel() = CheckBlockModel(
        memberId = this.memberId,
        name = this.name,
        blocked = this.blocked
    )
}
