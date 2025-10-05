package com.assu.app.domain.model.partnership

import org.threeten.bp.LocalDateTime

data class SuspendedPaperModel(
    val paperId: Long,
    val partnerName: String,
    val createdAt: LocalDateTime
)