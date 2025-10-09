package com.ssu.assu.data.dto.partnership.response

import com.ssu.assu.domain.model.partnership.SuspendedPaperModel
import com.squareup.moshi.JsonClass
import org.threeten.bp.LocalDateTime

@JsonClass(generateAdapter = true)
data class SuspendedPaperDto(
    val paperId: Long,
    val partnerName: String,
    val createdAt: LocalDateTime
) {
    fun toModel() = SuspendedPaperModel(
        paperId = paperId,
        partnerName = partnerName,
        createdAt = createdAt
    )
}