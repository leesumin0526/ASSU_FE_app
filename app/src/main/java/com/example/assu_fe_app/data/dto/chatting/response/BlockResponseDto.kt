package com.example.assu_fe_app.data.dto.chatting.response

import com.example.assu_fe_app.domain.model.chatting.BlockOpponentModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BlockResponseDto(
    val opponentId: Long,
    val opponentName: String
) {
    fun toModel() = BlockOpponentModel(
        opponentId = this.opponentId,
        opponentName = this.opponentName
    )
}
