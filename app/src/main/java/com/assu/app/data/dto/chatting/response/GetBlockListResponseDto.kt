package com.assu.app.data.dto.chatting.response

import com.squareup.moshi.JsonClass
import com.assu.app.domain.model.chatting.GetBlockListModel

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
