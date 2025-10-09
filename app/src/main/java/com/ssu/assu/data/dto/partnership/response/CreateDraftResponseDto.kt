package com.ssu.assu.data.dto.partnership.response

import com.ssu.assu.domain.model.partnership.CreateDraftResponseModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateDraftResponseDto(
    val paperId: Long
) {
    fun toModel() = CreateDraftResponseModel(paperId = this.paperId)
}
