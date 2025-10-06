package com.ssu.assu.domain.model.dashboard

data class AdminDashboardModel(
    val totalStudentCount: Int,
    val newStudentCount: Int,
    val todayUsagePersonCount: Int,
    val monthlyUsageCount: Int,
    val storeUsageStats: List<StoreUsageStat>
) {
    data class StoreUsageStat(
        val storeId: Long,
        val storeName: String,
        val usageCount: Long,
        val todayUsageCount: Int = 0,
        val monthlyUsageCount: Int = 0,
        val registrationDate: String = "정보 없음"
    )
}