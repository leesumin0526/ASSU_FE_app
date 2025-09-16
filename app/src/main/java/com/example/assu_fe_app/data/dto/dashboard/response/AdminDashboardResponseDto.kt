package com.example.assu_fe_app.data.dto.dashboard.response

import com.example.assu_fe_app.domain.model.dashboard.AdminDashboardModel
import com.example.assu_fe_app.domain.model.dashboard.StoreUsageModel
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AdminDashboardResponseDto(
    val adminId: Long,
    val adminName: String,
    val totalStudentCount: Long,      // 전체 제휴 학생 수
    val newStudentCount: Long,        // 이번 달 신규 학생 수
    val todayUsagePersonCount: Long,  // 오늘 이용한 고유 사용자 수
    val storeUsageStats: List<StoreUsageDto>  // 매장별 사용 통계
) {
    fun toModel() = AdminDashboardModel(
        adminId = adminId,
        adminName = adminName,
        totalStudentCount = totalStudentCount,
        newStudentCount = newStudentCount,
        todayUsagePersonCount = todayUsagePersonCount,
        storeUsageStats = storeUsageStats.map { it.toModel() }
    )
}

@JsonClass(generateAdapter = true)
data class StoreUsageDto(
    val storeId: Long,
    val storeName: String,
    val usageCount: Long
) {
    fun toModel() = StoreUsageModel(
        storeId = storeId,
        storeName = storeName,
        usageCount = usageCount
    )
}

// 개별 API 응답 DTO들
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
data class CountUsageListResponseDTO(
    val items: List<StoreUsageItem>
)

@JsonClass(generateAdapter = true)
data class StoreUsageItem(
    val usageCount: Long,
    val adminId: Long,
    val adminName: String,
    val storeId: Long,
    val storeName: String
)