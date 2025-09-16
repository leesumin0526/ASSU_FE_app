package com.example.assu_fe_app.data.dto.map

import java.time.LocalDate

data class AdminMapResponseDto (
    val adminId: Long,
    val name: String,
    val address: String,
    val partnered: Boolean,
    val partnershipId: Long?, // 제휴 상태가 아닐 경우 null일 수 있음
    val partnershipStartDate: LocalDate?, // 제휴 상태가 아닐 경우 null일 수 있음
    val partnershipEndDate: LocalDate?, // 제휴 상태가 아닐 경우 null일 수 있음
    val latitude: Double,
    val longitude: Double
)