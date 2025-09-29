package com.example.assu_fe_app.data.dto.chatting.response

import com.example.assu_fe_app.domain.model.chatting.CheckBlockModel
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
