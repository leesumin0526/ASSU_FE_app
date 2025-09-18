package com.example.assu_fe_app.domain.model.dashboard

data class AdminDashboardModel(
    val adminId: Long,
    val adminName: String,
    val totalStudentCount: Long,      // 전체 제휴 학생 수
    val newStudentCount: Long,        // 이번 달 신규 학생 수
    val todayUsagePersonCount: Long,  // 오늘 이용한 고유 사용자 수
    val storeUsageStats: List<StoreUsageModel>  // 매장별 사용 통계
) {
    fun getMyStoreUsage(storeId: Long): Long {
        return storeUsageStats.find { it.storeId == storeId }?.usageCount ?: 0L
    }

    fun getTotalUsageCount(): Long {
        return storeUsageStats.sumOf { it.usageCount }
    }

    fun getTopPerformingStores(limit: Int = 5): List<StoreUsageModel> {
        return storeUsageStats.sortedByDescending { it.usageCount }.take(limit)
    }
}

data class StoreUsageModel(
    val storeId: Long,
    val storeName: String,
    val usageCount: Long
)