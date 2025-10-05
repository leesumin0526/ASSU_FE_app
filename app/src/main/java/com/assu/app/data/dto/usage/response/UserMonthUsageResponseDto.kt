package com.assu.app.data.dto.usage.response

data class UserMonthUsageResponseDto(
    val details: List<Detail>,
    val serviceCount: Long
)