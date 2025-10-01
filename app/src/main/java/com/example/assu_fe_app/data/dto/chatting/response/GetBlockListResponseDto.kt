package com.example.assu_fe_app.data.dto.chatting.response

import com.squareup.moshi.JsonClass
import com.example.assu_fe_app.domain.model.chatting.GetBlockListModel

@JsonClass(generateAdapter = true)
data class GetBlockListResponseDto(
    val memberId: Long,
    val name: String,
    val blockDate: String,
) {
    fun toModel() = GetBlockListModel (
        memberId = this.memberId,
        name = this.name,
        blockDate = this.blockDate
    )
}
