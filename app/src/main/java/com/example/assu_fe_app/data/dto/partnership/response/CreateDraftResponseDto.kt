package com.example.assu_fe_app.data.dto.partnership.response

import com.example.assu_fe_app.domain.model.partnership.CreateDraftResponseModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateDraftResponseDto(
    val paperId: Long
) {
    fun toModel() = CreateDraftResponseModel(paperId = this.paperId)
}
