package com.assu.app.data.dto.partnership.response

import com.assu.app.domain.model.partnership.CreateDraftResponseModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateDraftResponseDto(
    val paperId: Long
) {
    fun toModel() = CreateDraftResponseModel(paperId = this.paperId)
}
