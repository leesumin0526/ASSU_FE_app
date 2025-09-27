package com.example.assu_fe_app.data.dto.chatting.response

import com.example.assu_fe_app.domain.model.chatting.BlockOpponentModel
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
