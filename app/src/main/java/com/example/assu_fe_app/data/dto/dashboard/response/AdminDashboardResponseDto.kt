package com.example.assu_fe_app.data.dto.dashboard.response

import com.example.assu_fe_app.domain.model.dashboard.AdminDashboardModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CountAdminAuthResponseDTO(
    val studentCount: Long,
    val adminId: Long,
    val adminName: String
)

@JsonClass(generateAdapter = true)
data class NewCountAdminResponseDTO(
    val newStudentCount: Long,
    val adminId: Long,
    val adminName: String
)

@JsonClass(generateAdapter = true)
data class CountUsagePersonResponseDTO(
    val usagePersonCount: Long,
    val adminId: Long,
    val adminName: String
)

@JsonClass(generateAdapter = true)
data class MonthlyUsageCountResponseDTO(
    val monthlyUsageCount: Long,
    val adminId: Long,
    val adminName: String
)

@JsonClass(generateAdapter = true)
data class CountUsageResponseDTO( // 제휴 업체별 누적 제휴 이용현황
    val usageCount: Long,
    val adminId: Long,
    val adminName: String,
    val storeId: Long,
    val storeName: String,
    val todayUsageCount: Long? = null,
    val monthlyUsageCount: Long? = null,
    val registrationDate: String? = null
) {
    fun toStoreUsageStat() = AdminDashboardModel.StoreUsageStat(
        storeId = storeId,
        storeName = storeName,
        usageCount = usageCount,
        todayUsageCount = (todayUsageCount ?: 0L).toInt(),
        monthlyUsageCount = (monthlyUsageCount ?: 0L).toInt(),
        registrationDate = registrationDate ?: "정보 없음"
    )
}

@JsonClass(generateAdapter = true)
data class CountUsageListResponseDTO(
    val items: List<CountUsageResponseDTO> // 사용량 내림차순 정렬됨
)

// 기존 통합용 DTO는 삭제하거나 주석처리
/*
@JsonClass(generateAdapter = true)
data class AdminDashboardResponseDto(
    val adminId: Long,
    val adminName: String,
    val totalStudentCount: Long,
    val newStudentCount: Long,
    val todayUsagePersonCount: Long,
    val monthlyUsageCount: Long,
    val storeUsageStats: List<StoreUsageStatsDto>
)

@JsonClass(generateAdapter = true)
data class StoreUsageStatsDto(
    val usageCount: Long,
    val adminId: Long,
    val adminName: String,
    val storeId: Long,
    val storeName: String,
    val todayUsageCount: Long,
    val monthlyUsageCount: Long,
    val registrationDate: String
)*/