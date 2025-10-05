package com.assu.app.data.dto.dashboard.response

import com.assu.app.domain.model.dashboard.WeeklyRankModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeeklyRankDto(
    val rank: Long,
    val usageCount: Long
) {
    fun toModel() = WeeklyRankModel(
        rank = rank,
        usageCount = usageCount
    )
}