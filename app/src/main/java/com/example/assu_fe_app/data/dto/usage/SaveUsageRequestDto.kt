package com.example.assu_fe_app.data.dto.usage

data class SaveUsageRequestDto(
    val adminName: String,
    val contentId: Long,
    val discount: Long,
    val partnershipContent: String,
    val placeName: String,
    val userIds: List<Long>
)